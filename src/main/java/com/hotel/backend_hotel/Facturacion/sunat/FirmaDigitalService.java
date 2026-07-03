package com.hotel.backend_hotel.Facturacion.sunat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class FirmaDigitalService {

    private static final Logger log = LoggerFactory.getLogger(FirmaDigitalService.class);
    private static final String SUNAT_SIGNATURE_ID = "SignatureSP";

    @Value("${sunat.cert.path:core/certs/DEMO_Sunat.pfx}")
    private String certPath;

    @Value("${sunat.cert.password:}")
    private String certPassword;

    @Value("${sunat.storage.dir:storage/xmls}")
    private String storageDir;

    public byte[] firmarXml(byte[] xmlBytes, String nombreComprobante) throws Exception {
        Path certFile = Paths.get(certPath);
        if (!Files.exists(certFile)) {
            throw new IllegalStateException("Certificado no encontrado: " + certFile.toAbsolutePath());
        }

        KeyStore ks = KeyStore.getInstance("PKCS12");
        char[] pwd = certPassword != null ? certPassword.toCharArray() : new char[0];
        try (FileInputStream fis = new FileInputStream(certFile.toFile())) {
            ks.load(fis, pwd);
        }

        String alias = ks.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, pwd);
        Certificate cert = ks.getCertificate(alias);
        X509Certificate x509Cert = (X509Certificate) cert;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xmlBytes));

        NodeList extContentList = doc.getElementsByTagNameNS(
                "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2",
                "ExtensionContent");
        if (extContentList.getLength() == 0) {
            throw new IllegalStateException("No se encontró ext:ExtensionContent en el XML UBL");
        }
        Element extensionContent = (Element) extContentList.item(0);
        while (extensionContent.hasChildNodes()) {
            extensionContent.removeChild(extensionContent.getFirstChild());
        }

        XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM");

        Reference ref = sigFactory.newReference(
                "",
                sigFactory.newDigestMethod(DigestMethod.SHA256, null),
                List.of(
                        sigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
                        sigFactory.newTransform(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null)
                ),
                null,
                null);

        SignedInfo signedInfo = sigFactory.newSignedInfo(
                sigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                List.of(ref));

        KeyInfoFactory kif = sigFactory.getKeyInfoFactory();
        X509Data x509Data = kif.newX509Data(List.of(x509Cert));
        KeyInfo keyInfo = kif.newKeyInfo(List.of(x509Data));

        DOMSignContext signContext = new DOMSignContext(privateKey, extensionContent);
        signContext.setDefaultNamespacePrefix("ds");
        signContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ds");

        XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo, null, SUNAT_SIGNATURE_ID, null);
        try {
            signature.sign(signContext);
        } catch (MarshalException | XMLSignatureException e) {
            throw new RuntimeException("Error al firmar XML", e);
        }

        extensionContent.appendChild(doc.renameNode(
                extensionContent.getFirstChild(), "http://www.w3.org/2000/09/xmldsig#", "ds:Signature"));

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String signed = writer.toString();

        signed = signed.replace("standalone=\"no\"", "");
        signed = signed.replace("'", "\"");

        byte[] firmadoBytes = signed.getBytes(StandardCharsets.UTF_8);

        Path firmadosDir = Paths.get(storageDir, "firmados");
        Files.createDirectories(firmadosDir);
        Files.write(firmadosDir.resolve(nombreComprobante + ".xml"), firmadoBytes);

        return firmadoBytes;
    }

    public String extraerRucDelCertificado() {
        try {
            Path certFile = Paths.get(certPath);
            if (!Files.exists(certFile)) return null;

            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] pwd = certPassword != null ? certPassword.toCharArray() : new char[0];
            try (FileInputStream fis = new FileInputStream(certFile.toFile())) {
                ks.load(fis, pwd);
            }

            String alias = ks.aliases().nextElement();
            Certificate cert = ks.getCertificate(alias);
            if (cert instanceof X509Certificate x509) {
                String subject = x509.getSubjectX500Principal().getName();
                var matcher = java.util.regex.Pattern.compile("(\\d{11})").matcher(subject);
                if (matcher.find()) return matcher.group(1);
                String cn = x509.getSubjectX500Principal().getName();
                matcher = java.util.regex.Pattern.compile("(\\d{11})").matcher(cn);
                if (matcher.find()) return matcher.group(1);
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer RUC del certificado: {}", e.getMessage());
        }
        return null;
    }
}
