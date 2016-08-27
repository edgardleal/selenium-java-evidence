package com.eliasnogueira.selenium.evidence.report;

import com.eliasnogueira.selenium.evidence.EvidenceReport;
import com.eliasnogueira.selenium.evidence.EvidenceType;
import com.eliasnogueira.selenium.evidence.SeleniumEvidence;
import com.eliasnogueira.selenium.evidence.utils.SeleniumEvidenceUtils;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Generate the test evidence in PDF file
 *
 * @author Elias Nogueira <elias.nogueira@gmail.com>
 */
public class GenerateEvidenceReport {
  private String evidenceDir;

  public GenerateEvidenceReport() {
    evidenceDir =
        SeleniumEvidenceUtils.get("evidence.dir") + System.getProperty("file.separator");
    createEvidenceDir(evidenceDir);
  }

  private Map<String, Object> setupReportParameters(EvidenceReport evidenceReport) throws IOException {
    Map<String, Object> parameters = new HashMap<String, Object>();
    String companyImage = SeleniumEvidenceUtils.get("image.company.path", null);
    String customerImage = SeleniumEvidenceUtils.get("image.customer.path", null);

    BufferedImage imageCompany = null;
    BufferedImage imageClient = null;

    if (companyImage != null) {
      imageCompany = ImageIO.read(new File(companyImage));
    }

    if (customerImage != null) {
      imageClient = ImageIO.read(new File(customerImage));
    }

    String tester = evidenceReport.getTester();
    String project = evidenceReport.getProject();
    String exception = evidenceReport.getExceptionString();

    parameters.put("SEL_EXCEPTION", exception);
    parameters.put("SEL_COMPANY_LOGO", imageCompany);
    parameters.put("SEL_CUSTOMER_LOGO", imageClient);
    parameters.put("SEL_PROJECT", project);
    parameters.put("SEL_TESTER", tester);

    parameters.put("SEL_LABEL_EVINDENCE_TITLE", SeleniumEvidenceUtils.get("label.evidenceReport"));
    parameters.put("SEL_LABEL_PROJECT", SeleniumEvidenceUtils.get("label.projetc"));
    parameters.put("SEL_LABEL_TESTER", SeleniumEvidenceUtils.get("label.tester"));
    parameters.put("SEL_LABEL_STATUS", SeleniumEvidenceUtils.get("label.status"));
    parameters.put("SEL_LABEL_PASS", SeleniumEvidenceUtils.get("label.status.pass"));
    parameters.put("SEL_LABEL_FAILED", SeleniumEvidenceUtils.get("label.status.failed"));
    parameters.put("SEL_LABEL_EVIDENCE_REPORT", SeleniumEvidenceUtils.get("label.evidenceReport"));
    parameters.put("SEL_LABEL_DATE", SeleniumEvidenceUtils.get("label.date"));
    parameters.put("SEL_LABEL_FOOTER", SeleniumEvidenceUtils.get("label.footer"));
    parameters.put("SEL_LABEL_ERROR_DETAIL", SeleniumEvidenceUtils.get("label.errorDetail"));
    parameters.put("SEL_LABEL_PAGE", SeleniumEvidenceUtils.get("label.page"));
    parameters.put("SEL_LABEL_COMPANY_NAME", SeleniumEvidenceUtils.get("label.company.name"));
    return parameters;
  }


  /**
   * Generate an evidence report based on EvidenceType (DOC, PDF, HTML)
   *
   * @param evidenceReport and EvidenceReport object with basic informations
   * @param reportType     filetype
   * @throws IOException if any problem with the files (jasper, EvidenceType) or directory occurs
   */
  public void generareEvidenceReport(EvidenceReport evidenceReport, EvidenceType reportType) throws IOException {
    List<SeleniumEvidence> data = evidenceReport.getEvidenceList();

    try {

      Map<String, Object> parameters =
      setupReportParameters(evidenceReport);

      JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(data);


      JasperPrint jasperPrint = JasperFillManager.fillReport(SeleniumEvidenceUtils.get("evidence.file"), parameters, datasource);

      switch (reportType) {
        case PDF:
          JasperExportManager.exportReportToPdfFile(jasperPrint, generateFileName(evidenceReport));
          break;

        case DOC:
          JRDocxExporter exporter = new JRDocxExporter();

          File file = new File(generateFileName(evidenceReport) + ".doc");
          FileOutputStream os = new FileOutputStream(file);

          exporter.setParameter(JRDocxExporterParameter.JASPER_PRINT, jasperPrint);
          exporter.setParameter(JRDocxExporterParameter.CHARACTER_ENCODING, "UTF-8");
          exporter.setParameter(JRDocxExporterParameter.OUTPUT_STREAM, os);

          exporter.exportReport();

          os.close();
          break;

        case HTML:
          JasperExportManager.exportReportToHtmlFile(jasperPrint, generateFileName(evidenceReport) + ".html");
          break;

        default:
          break;
      }

    } catch (SecurityException ex) {
      ex.printStackTrace();
    } catch (JRException jre) {
      jre.printStackTrace();
    }
  }

  private String generateFileName(EvidenceReport evidenceReport) {
    return this.evidenceDir + evidenceReport.getReportName();
  }

  /**
   * Create a directory with the project's name
   *
   * @param directory directory to be checked
   */
  private static boolean createEvidenceDir(String directory) {
    boolean dirExists = false;

    try {
      File dir = new File(directory);
      boolean exists = dir.exists();

      if (!exists) {
        dir.mkdirs();
        dirExists = false;
      } else {
        dirExists = true;
      }
    } catch (SecurityException se) {
      se.printStackTrace();
    }
    return dirExists;
  }
}
