<?xml version="1.0" encoding="UTF-8"?>
<Invoice xmlns="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
         xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
         xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"
         xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"
         xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
    <ext:UBLExtensions>
        <ext:UBLExtension>
            <ext:ExtensionContent/>
        </ext:UBLExtension>
    </ext:UBLExtensions>
    <cbc:UBLVersionID>2.1</cbc:UBLVersionID>
    <cbc:CustomizationID>2.0</cbc:CustomizationID>
    <cbc:ProfileID>0101</cbc:ProfileID>
    <cbc:ID>${serieNum}</cbc:ID>
    <cbc:IssueDate>${fechaEmision}</cbc:IssueDate>
    <cbc:InvoiceTypeCode listAgencyName="PE:SUNAT" listName="Tipo de Documento" listURI="urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01" listID="0101" listSchemeURI="urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo51" name="Tipo de Operacion">${tipoComprobante}</cbc:InvoiceTypeCode>
    <cbc:Note languageLocaleID="1000"><![CDATA[${totalLetras}]]></cbc:Note>
    <cbc:DocumentCurrencyCode>${moneda}</cbc:DocumentCurrencyCode>
    <cac:Signature>
        <cbc:ID>${serieNum}</cbc:ID>
        <cac:SignatoryParty>
            <cac:PartyIdentification>
                <cbc:ID>${rucEmisor}</cbc:ID>
            </cac:PartyIdentification>
            <cac:PartyName>
                <cbc:Name><![CDATA[${razonSocialEmisor}]]></cbc:Name>
            </cac:PartyName>
        </cac:SignatoryParty>
        <cac:DigitalSignatureAttachment>
            <cac:ExternalReference>
                <cbc:URI>#SignatureSP</cbc:URI>
            </cac:ExternalReference>
        </cac:DigitalSignatureAttachment>
    </cac:Signature>
    <cac:AccountingSupplierParty>
        <cac:Party>
            <cac:PartyIdentification>
                <cbc:ID schemeID="6">${rucEmisor}</cbc:ID>
            </cac:PartyIdentification>
            <cac:PartyTaxScheme>
                <cbc:RegistrationName><![CDATA[${razonSocialEmisor}]]></cbc:RegistrationName>
                <cbc:CompanyID schemeID="6">${rucEmisor}</cbc:CompanyID>
                <cac:TaxScheme><cbc:ID>${rucEmisor}</cbc:ID></cac:TaxScheme>
            </cac:PartyTaxScheme>
            <cac:PartyLegalEntity>
                <cbc:RegistrationName><![CDATA[${razonSocialEmisor}]]></cbc:RegistrationName>
                <cac:RegistrationAddress>
                    <cbc:ID>${ubigeoEmisor}</cbc:ID>
                    <cbc:AddressTypeCode>0000</cbc:AddressTypeCode>
                    <cbc:CityName><![CDATA[${ciudadEmisor}]]></cbc:CityName>
                    <cbc:CountrySubentity><![CDATA[${departamentoEmisor}]]></cbc:CountrySubentity>
                    <cac:AddressLine><cbc:Line><![CDATA[${direccionEmisor}]]></cbc:Line></cac:AddressLine>
                    <cac:Country><cbc:IdentificationCode>PE</cbc:IdentificationCode></cac:Country>
                </cac:RegistrationAddress>
            </cac:PartyLegalEntity>
        </cac:Party>
    </cac:AccountingSupplierParty>
    <cac:AccountingCustomerParty>
        <cac:Party>
            <cac:PartyIdentification>
                <cbc:ID schemeID="${clienteTipoDoc}">${clienteNumeroDoc}</cbc:ID>
            </cac:PartyIdentification>
            <#if clienteTipoDoc == "6">
            <cac:PartyTaxScheme>
                <cbc:RegistrationName><![CDATA[${clienteRazonSocial}]]></cbc:RegistrationName>
                <cbc:CompanyID schemeID="6">${clienteNumeroDoc}</cbc:CompanyID>
                <cac:TaxScheme><cbc:ID>-</cbc:ID></cac:TaxScheme>
            </cac:PartyTaxScheme>
            </#if>
            <cac:PartyLegalEntity>
                <cbc:RegistrationName><![CDATA[${clienteRazonSocial}]]></cbc:RegistrationName>
                <cac:RegistrationAddress>
                    <cac:AddressLine><cbc:Line><![CDATA[${clienteDireccion}]]></cbc:Line></cac:AddressLine>
                    <cac:Country><cbc:IdentificationCode>PE</cbc:IdentificationCode></cac:Country>
                </cac:RegistrationAddress>
            </cac:PartyLegalEntity>
        </cac:Party>
    </cac:AccountingCustomerParty>
    <cac:PaymentTerms>
        <cbc:ID>FormaPago</cbc:ID>
        <cbc:PaymentMeansID>Contado</cbc:PaymentMeansID>
    </cac:PaymentTerms>
    <cac:TaxTotal>
        <cbc:TaxAmount currencyID="${moneda}">${totalIgv?string("0.00")}</cbc:TaxAmount>
        <cac:TaxSubtotal>
            <cbc:TaxableAmount currencyID="${moneda}">${totalOpGravadas?string("0.00")}</cbc:TaxableAmount>
            <cbc:TaxAmount currencyID="${moneda}">${totalIgv?string("0.00")}</cbc:TaxAmount>
            <cac:TaxCategory>
                <cbc:ID>S</cbc:ID>
                <cac:TaxScheme>
                    <cbc:ID>1000</cbc:ID>
                    <cbc:Name>IGV</cbc:Name>
                    <cbc:TaxTypeCode>VAT</cbc:TaxTypeCode>
                </cac:TaxScheme>
            </cac:TaxCategory>
        </cac:TaxSubtotal>
    </cac:TaxTotal>
    <cac:LegalMonetaryTotal>
        <cbc:LineExtensionAmount currencyID="${moneda}">${totalOpGravadas?string("0.00")}</cbc:LineExtensionAmount>
        <cbc:TaxInclusiveAmount currencyID="${moneda}">${total?string("0.00")}</cbc:TaxInclusiveAmount>
        <cbc:PayableAmount currencyID="${moneda}">${total?string("0.00")}</cbc:PayableAmount>
    </cac:LegalMonetaryTotal>
<#list detalles as det>
    <cac:InvoiceLine>
        <cbc:ID>${det.item}</cbc:ID>
        <cbc:InvoicedQuantity unitCode="${det.unidadMedida}">${det.cantidad?string("0.00")}</cbc:InvoicedQuantity>
        <cbc:LineExtensionAmount currencyID="${moneda}">${det.valorTotal?string("0.00")}</cbc:LineExtensionAmount>
        <cac:PricingReference>
            <cac:AlternativeConditionPrice>
                <cbc:PriceAmount currencyID="${moneda}">${det.precioUnitario?string("0.00")}</cbc:PriceAmount>
                <cbc:PriceTypeCode>01</cbc:PriceTypeCode>
            </cac:AlternativeConditionPrice>
        </cac:PricingReference>
        <cac:TaxTotal>
            <cbc:TaxAmount currencyID="${moneda}">${det.igv?string("0.00")}</cbc:TaxAmount>
            <cac:TaxSubtotal>
                <cbc:TaxableAmount currencyID="${moneda}">${det.valorTotal?string("0.00")}</cbc:TaxableAmount>
                <cbc:TaxAmount currencyID="${moneda}">${det.igv?string("0.00")}</cbc:TaxAmount>
                <cac:TaxCategory>
                    <cbc:ID>S</cbc:ID>
                    <cbc:Percent>${det.porcentajeIgv?string("0.00")}</cbc:Percent>
                    <cbc:TaxExemptionReasonCode listAgencyName="PE:SUNAT" listName="Afectacion del IGV" listURI="urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo07">10</cbc:TaxExemptionReasonCode>
                    <cac:TaxScheme>
                        <cbc:ID>1000</cbc:ID>
                        <cbc:Name>IGV</cbc:Name>
                        <cbc:TaxTypeCode>VAT</cbc:TaxTypeCode>
                    </cac:TaxScheme>
                </cac:TaxCategory>
            </cac:TaxSubtotal>
        </cac:TaxTotal>
        <cac:Item>
            <cbc:Description><![CDATA[${det.descripcion}]]></cbc:Description>
        </cac:Item>
        <cac:Price>
            <cbc:PriceAmount currencyID="${moneda}">${det.valorUnitario?string("0.00")}</cbc:PriceAmount>
        </cac:Price>
    </cac:InvoiceLine>
</#list>
</Invoice>
