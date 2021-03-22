package com.tpcodl.billingreading.utils;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.Proxy;

import static com.tpcodl.billingreading.utils.Constant.uploadPath;


public class UploadImage {
    private SoapObject soapObject;
    String outputArray;

    public String uploadFile(String fileName, String base64){
        soapObject = new SoapObject(Constant.baseUrlImage, Constant.postUrlImage);
        soapObject.addProperty("img_name", fileName);
        soapObject.addProperty("img_base64 ", base64);
        //D:\Production\webservice_android\img
        soapObject.addProperty("save_path", uploadPath);
        //soapObject.addProperty("save_path", "D:\\Production\\webservice_android\\img");

        outputArray = Output(soapObject, Constant.baseUrlImage + Constant.postUrlImage);
        return outputArray;
    }

    private String Output(SoapObject soapObject, final String SOAP_ACTION) {
        String output = "";

        SoapSerializationEnvelope soapSerializationEnvelope = getSoapSerializationEnvelop(soapObject);

        HttpTransportSE httpTransportSE = new HttpTransportSE(Proxy.NO_PROXY, Constant.uploadImageurl, 60000);
        httpTransportSE.debug = true;
        try {
            httpTransportSE.call(SOAP_ACTION, soapSerializationEnvelope);
            //get the response
            SoapPrimitive soapPrimitive = (SoapPrimitive) soapSerializationEnvelope.getResponse();
            if (soapPrimitive != null) {
                output = soapPrimitive.toString();
            } else {
                return null;
            }
        } catch (IOException | XmlPullParserException ioe) {
            ioe.printStackTrace();
        }
        return output;
    }

    private SoapObject OutputArray(SoapObject soapObject, final String SOAP_ACTION) {
        SoapObject outputArray = null;
        SoapSerializationEnvelope soapSerializationEnvelope = getSoapSerializationEnvelop(soapObject);
        HttpTransportSE httpTransportSE = new HttpTransportSE(Proxy.NO_PROXY, Constant.uploadImageurl, 60000);
        httpTransportSE.debug = true;
        try {
            httpTransportSE.call(SOAP_ACTION, soapSerializationEnvelope);
            //get the response
            outputArray = (SoapObject) soapSerializationEnvelope.getResponse();
        } catch (IOException | XmlPullParserException ioe) {
            ioe.printStackTrace();
        }

        return outputArray;
    }
    private static SoapSerializationEnvelope getSoapSerializationEnvelop(SoapObject soapObject) {
        SoapSerializationEnvelope soapSerializationEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapSerializationEnvelope.dotNet = true;
        soapSerializationEnvelope.implicitTypes = true;
        soapSerializationEnvelope.setAddAdornments(false);
        soapSerializationEnvelope.setOutputSoapObject(soapObject);
        return soapSerializationEnvelope;
    }


}
