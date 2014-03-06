package com.ctriposs.blacksmith.config;

import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * @author bulldog
 */
public class DomConfigParser extends ConfigParser {

   public static void addDirectAttributes(Properties properties, Element element, String prefix) {
      NamedNodeMap attributes = element.getAttributes();
      for (int j = 0; j < attributes.getLength(); j++) {
         String name = attributes.item(j).getNodeName();
         String value = attributes.item(j).getNodeValue();
         properties.put(prefix + name, value);
      }
   }

   public static void addWrapperAttributes(Properties properties, Element element, String prefix) {
      NodeList childList = element.getChildNodes();
      for (int i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i) instanceof Element) {
            Element child = (Element) childList.item(i);
            if (child.getNodeName().equalsIgnoreCase("wrapper")) {
               String wrapperClass = child.getAttribute("class");
               if (wrapperClass != null && !wrapperClass.isEmpty()) {
                  properties.setProperty(prefix + "wrapper", wrapperClass);
               }
               NodeList wrapperProps = child.getChildNodes();
               for (int j = 0; j < wrapperProps.getLength(); ++j) {
                  if (wrapperProps.item(j) instanceof Element) {
                     Element prop = (Element) wrapperProps.item(j);
                     if (!prop.getNodeName().equalsIgnoreCase("property")) {
                        throw new IllegalArgumentException();
                     }
                     String name = prop.getAttribute("name");
                     String value = prop.getAttribute("value");
                     if (name == null || name.isEmpty()) throw new IllegalArgumentException();
                     properties.setProperty(prefix + name, value);
                  }
               }
            }
         }
      }
   }
}
