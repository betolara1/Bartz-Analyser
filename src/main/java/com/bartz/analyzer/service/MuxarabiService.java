package com.bartz.analyzer.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class MuxarabiService {
    
    public boolean temMuxarabi(Document doc){
        try{
            NodeList muxarabi = doc.getElementsByTagName("ITEM");

            for(int i = 0; i < muxarabi.getLength(); i++){
                Element muxarabiElement = (Element) muxarabi.item(i);
                String refMuxarabi = muxarabiElement.getAttribute("REFERENCIA");

                if(refMuxarabi.startsWith("MX6")) {
                    return true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
