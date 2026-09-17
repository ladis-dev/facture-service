package com.example.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import jakarta.persistence.EntityManager;

import org.mustangproject.Invoice;
import org.mustangproject.Item;
import org.mustangproject.Product;
import org.mustangproject.TradeParty;
import org.mustangproject.ZUGFeRD.Profiles;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporterFromA3;

import com.example.dao.FactureRepository;
import com.example.dao.FileRepository;
import com.example.domain.StatutFacture;
import com.example.entity.FactureEntity;
import com.example.entity.FileEntity;
import com.example.entity.ProduitFactureEntity;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.example.dto.FileDto;

public class FactureGenerationService {

    private static final Path STORAGE_DIR = Path.of("factures-generees");

    private final EntityManager em;
    private final FactureRepository factureRepository;
    private final FileRepository fileRepository;

    public FactureGenerationService(FactureRepository factureRepository,
            FileRepository fileRepository, EntityManager em) {
        this.em = em;
        this.factureRepository = factureRepository;
        this.fileRepository = fileRepository;
    }

    public FileDto genererFacturX(Long factureId) {

        FactureEntity facture = factureRepository.findById(factureId);
        if (facture == null) {
            throw new IllegalArgumentException("Facture introuvable avec l'ID : " + factureId);
        }

        if (facture.getStatut() == StatutFacture.CREATION) {
            throw new IllegalStateException(
                    "Impossible de générer un fichier pour une facture encore en creation");
        }

        Path pdfFinalPath = genererFichierPdf(facture, factureId);

        try {
            return TransactionUtil.runInTransaction(em, () -> {
                // Enregistrer le fichier en base (table fichiers)
                FileEntity fileEntity = new FileEntity(facture, "FACTURX_PDF", pdfFinalPath.toString());
                fileRepository.create(fileEntity);
                return FileDto.fromEntity(fileEntity);
            });
        } catch (RuntimeException e) {
            // l'écriture en base a échoué : on ne laisse pas un fichier orphelin sur le
            // disque
            try {
                Files.deleteIfExists(pdfFinalPath);
            } catch (IOException cleanupError) {
                e.addSuppressed(cleanupError);
            }
            throw e;
        }

    }

    private Path genererFichierPdf(FactureEntity facture, Long factureId) {
        try {
            Files.createDirectories(STORAGE_DIR);

            // 1. PDF visuel basique
            Path pdfPdfA1Path = STORAGE_DIR.resolve("facture-" + factureId + "-pdfa1.pdf");
            genererPdfPdfA1(facture, pdfPdfA1Path);

            // 2. Objet Invoice Mustang (pour générer le XML CII)
            Invoice invoice = construireInvoiceMustang(facture);

            // 3. Fusion PDF + XML -> Factur-X
            Path pdfFinalPath = STORAGE_DIR.resolve("facture-" + factureId + "-facturx.pdf");

            // try (ZUGFeRDExporterFromA1 exporter = new ZUGFeRDExporterFromA1()) {
            try (ZUGFeRDExporterFromA3 exporter = new ZUGFeRDExporterFromA3()) {
                exporter.load(pdfPdfA1Path.toString())
                        .setZUGFeRDVersion(2)
                        .setProfile(Profiles.getByName("EN16931"));
                exporter.setTransaction(invoice);
                exporter.export(pdfFinalPath.toString());
            }

            Files.deleteIfExists(pdfPdfA1Path);
            return pdfFinalPath;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du fichier PDF", e);
        }
    }

    private void genererPdfPdfA1(FactureEntity facture, Path destination) throws IOException {
        String html = construireHtml(facture);

        try (OutputStream os = new FileOutputStream(destination.toFile())) {
            PdfRendererBuilder builder = new PdfRendererBuilder();

            // builder.useFastMode();
            builder.withHtmlContent(html, null);

            // Police embarquée obligatoire
            final String fontPath = "/fonts/dejavu-sans/DejaVuSans.ttf";
            try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
                if (fontStream == null) {
                    throw new IllegalStateException("Police introuvable : " + fontPath);
                }
                builder.useFont(() -> getClass().getResourceAsStream(fontPath), "DejaVu Sans");
            }

            // Profil couleur ICC obligatoire
            final String iccPath = "/icc/sRGB2014.icc";
            try (InputStream iccStream = getClass().getResourceAsStream(iccPath)) {
                if (iccStream == null) {
                    throw new IllegalStateException("icc introuvable : " + iccPath);
                }
                builder.useColorProfile(iccStream.readAllBytes());
            }

            // builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_1_B);

            builder.toStream(os);
            builder.run();
        }
    }

    private String construireHtml(FactureEntity facture) {
        StringBuilder lignes = new StringBuilder();
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (ProduitFactureEntity ligne : facture.getProduits()) {
            BigDecimal totalLigne = ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantite()));

            totalGeneral = totalGeneral.add(totalLigne);

            lignes.append("<tr><td>").append(ligne.getProduit().getLibelle())
                    .append("</td><td>").append(ligne.getQuantite())
                    .append("</td><td>").append(ligne.getPrixUnitaire()).append(" EUR</td>")
                    .append("<td>").append(totalLigne).append(" EUR</td></tr>");
        }

        return """
                <html>
                <head>
                    <title>Facture %d</title>
                    <style>
                        body { font-family: 'DejaVu Sans'; }
                        table { width: 100%%; border-collapse: collapse; }
                        td, th { border: 1px solid #ccc; padding: 4px; text-align: left; }
                    </style>
                </head>
                <body>
                    <h1>Facture n° %d</h1>
                    <p>Client : %s %s</p>
                    <p>Statut : %s</p>
                    <table>
                        <thead>
                            <tr>
                                <th>Produit</th>
                                <th>Qté</th>
                                <th>Prix U.</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                        <tfoot>
                            <tr>
                                <td colspan="3" class="total-label">Total Général :</td>
                                <td><strong>%s EUR</strong></td>
                            </tr>
                        </tfoot>
                    </table>
                </body>
                </html>
                """.formatted(
                facture.getId(),
                facture.getId(),
                facture.getClient().getNom(),
                facture.getClient().getPrenom(),
                facture.getStatut(),
                lignes.toString(),
                totalGeneral);
    }

    private Invoice construireInvoiceMustang(FactureEntity facture) {
        Date issueDate = new Date();
        Date dueDate = Date.from(issueDate.toInstant().plus(30, ChronoUnit.DAYS));

        Invoice invoice = new Invoice()
                .setNumber("FAC-" + facture.getId())
                .setIssueDate(issueDate)
                .setDueDate(dueDate)
                .setSender(new TradeParty(
                        "Ma Société",
                        "1 rue de l'Exemple",
                        "75000",
                        "Paris",
                        "FR")
                        .addVATID("FR00000000000"))
                .setRecipient(new TradeParty(
                        facture.getClient().getNom() + " " + facture.getClient().getPrenom(),
                        facture.getClient().getAdresseFacturation(),
                        "", "",
                        "FR"));

        for (ProduitFactureEntity ligne : facture.getProduits()) {
            invoice.addItem(new Item(
                    new Product(ligne.getProduit().getLibelle(), ligne.getProduit().getDescription(), "C62",
                            BigDecimal.ZERO),
                    ligne.getPrixUnitaire(),
                    BigDecimal.valueOf(ligne.getQuantite())));
        }

        return invoice;
    }
}