package com.svm.ecom.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomerSwaggerUiController {


  // Public Swagger UI - Clean version without servers/contact info
  @GetMapping("/public/swagger-ui.html")
  @ResponseBody
  public String publicSwaggerUi() {
    return generateCleanSwaggerUi(
        "ECOM API - Public Documentation",
        Arrays.asList(
            createApiGroup("ECOM API - Schedule", "/v3/api-docs/schedule"),
            createApiGroup("ECOM API - Tracking", "/v3/api-docs/tracking"),
            createApiGroup("ECOM API - Master", "/v3/api-docs/master"),
            createApiGroup("ECOM API - Authenticate", "/v3/api-docs/authenticate")
        )
    );
  }

  // Customer Swagger UI using CDN resources for reliability
  @GetMapping("/customer/swagger-ui.html")
  @ResponseBody
  public String customerSwaggerUi() {
    return generateCleanSwaggerUi(
        "ECOM API - Customer Documentation",
        Arrays.asList(
            createApiGroup("ECOM API - Coastal and Long Range Schedule", "/v3/api-docs/schedule-coastal"),
            createApiGroup("ECOM API - SIBL (Bill of Lading)", "/v3/api-docs/sibl"),
            createApiGroup("ECOM API - Bill of Lading (V2)", "/v3/api-docs/bill-of-lading-v2"),
            createApiGroup("ECOM API - Booking", "/v3/api-docs/booking"),
            createApiGroup("ECOM API - Booking (V2)", "/v3/api-docs/booking-v2"),
            createApiGroup("ECOM API - Booking (V3)", "/v3/api-docs/booking-v3"),
            createApiGroup("ECOM API - Customer (V2)", "/v3/api-docs/customer-v2"),
            createApiGroup("ECOM API - Contract (V2)", "/v3/api-docs/contract-v2"),
            createApiGroup("ECOM API - Rates", "/v3/api-docs/rates"),
            createApiGroup("ECOM API - Summary", "/v3/api-docs/summary"),
            createApiGroup("ECOM API - Registration", "/v3/api-docs/registration"),
            createApiGroup("ECOM API - Customer Private", "/v3/api-docs/customer-private"),
            createApiGroup("ECOM API - Customer Gate", "/v3/api-docs/customer-gate")
        )
    );
  }

  // Helper method to create API group configuration
  private Map<String, String> createApiGroup(String name, String url) {
    Map<String, String> group = new HashMap<>();
    group.put("name", name);
    group.put("url", url);
    return group;
  }

  // Shared method to generate clean Swagger UI HTML
  private String generateCleanSwaggerUi(String title, java.util.List<Map<String, String>> apiGroups) {
    // Generate JavaScript array for URLs
    StringBuilder urlsJson = new StringBuilder("[");
    for (int i = 0; i < apiGroups.size(); i++) {
      if (i > 0) urlsJson.append(",");
      Map<String, String> group = apiGroups.get(i);
      urlsJson.append("{")
              .append("name: '").append(group.get("name")).append("',")
              .append("url: window.location.origin + '/ecomapi-service").append(group.get("url")).append("'")
              .append("}");
    }
    urlsJson.append("]");

    return "<!DOCTYPE html>\n" +
           "<html lang=\"en\">\n" +
           "<head>\n" +
           "    <meta charset=\"UTF-8\">\n" +
           "    <title>" + title + "</title>\n" +
           "    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://unpkg.com/swagger-ui-dist@4.15.5/swagger-ui.css\" />\n" +
           "    <link rel=\"icon\" type=\"image/png\" href=\"https://unpkg.com/swagger-ui-dist@4.15.5/favicon-32x32.png\" sizes=\"32x32\" />\n" +
           "    <style>\n" +
           "      html {\n" +
           "        box-sizing: border-box;\n" +
           "        overflow: -moz-scrollbars-vertical;\n" +
           "        overflow-y: scroll;\n" +
           "      }\n" +
           "      *,\n" +
           "      *:before,\n" +
           "      *:after {\n" +
           "        box-sizing: inherit;\n" +
           "      }\n" +
           "      body {\n" +
           "        margin:0;\n" +
           "        background: #fafafa;\n" +
           "      }\n" +
           "      /* Hide servers dropdown and contact info */\n" +
           "      .servers-wrapper,\n" +
           "      .servers,\n" +
           "      select[aria-label=\"Servers\"],\n" +
           "      .info .description,\n" +
           "      .info .contact,\n" +
           "      .info .contact a,\n" +
           "      .info .contact-name,\n" +
           "      .info .contact-email,\n" +
           "      .info .contact-url,\n" +
           "      .contact,\n" +
           "      .contact-name,\n" +
           "      .contact-email,\n" +
           "      .contact-url,\n" +
           "      .servers h4,\n" +
           "      .servers-title {\n" +
           "        display: none !important;\n" +
           "      }\n" +
           "    </style>\n" +
           "</head>\n" +
           "<body>\n" +
           "    <div id=\"swagger-ui\"></div>\n" +
           "    <script src=\"https://unpkg.com/swagger-ui-dist@4.15.5/swagger-ui-bundle.js\" charset=\"UTF-8\"></script>\n" +
           "    <script src=\"https://unpkg.com/swagger-ui-dist@4.15.5/swagger-ui-standalone-preset.js\" charset=\"UTF-8\"></script>\n" +
           "    <script>\n" +
           "    window.onload = function() {\n" +
           "      const ui = SwaggerUIBundle({\n" +
           "        urls: " + urlsJson.toString() + ",\n" +
           "        dom_id: '#swagger-ui',\n" +
           "        deepLinking: true,\n" +
           "        presets: [\n" +
           "          SwaggerUIBundle.presets.apis,\n" +
           "          SwaggerUIStandalonePreset\n" +
           "        ],\n" +
           "        plugins: [\n" +
           "          SwaggerUIBundle.plugins.DownloadUrl\n" +
           "        ],\n" +
           "        layout: \"StandaloneLayout\",\n" +
           "        validatorUrl: null,\n" +
           "        onComplete: function() {\n" +
           "          // Hide servers and contact information\n" +
           "          setTimeout(function() {\n" +
           "            // Hide servers elements\n" +
           "            const elements = [\n" +
           "              '.servers-wrapper', '.servers', 'select[aria-label=\"Servers\"]',\n" +
           "              '.info .description', '.info .contact', '.contact',\n" +
           "              '.contact-name', '.contact-email', '.contact-url',\n" +
           "              '.servers h4', '.servers-title'\n" +
           "            ];\n" +
           "            elements.forEach(selector => {\n" +
           "              const el = document.querySelector(selector);\n" +
           "              if (el) el.style.display = 'none';\n" +
           "            });\n" +
           "            // Hide any links containing 'ECOM API Support Team'\n" +
           "            document.querySelectorAll('a').forEach(link => {\n" +
           "              if (link.textContent.includes('ECOM API Support Team') || \n" +
           "                  link.textContent.includes('Send email to ECOM API Support Team')) {\n" +
           "                link.style.display = 'none';\n" +
           "              }\n" +
           "            });\n" +
           "          }, 100);\n" +
           "        }\n" +
           "      });\n" +
           "    };\n" +
           "    </script>\n" +
           "</body>\n" +
           "</html>";
  }


  // Customer config endpoint (backup)
  @GetMapping(value = "/customer/swagger-config", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Map<String, Object> customerSwaggerConfig() {
    java.util.List<Map<String, String>> urls = Arrays.asList(
        createApiGroup("ECOM API - Coastal and Long Range Schedule", "/v3/api-docs/schedule-coastal"),
        createApiGroup("ECOM API - SIBL (Bill of Lading)", "/v3/api-docs/sibl"),
        createApiGroup("ECOM API - Bill of Lading (V2)", "/v3/api-docs/bill-of-lading-v2"),
        createApiGroup("ECOM API - Booking", "/v3/api-docs/booking"),
        createApiGroup("ECOM API - Booking (V2)", "/v3/api-docs/booking-v2"),
        createApiGroup("ECOM API - Booking (V3)", "/v3/api-docs/booking-v3"),
        createApiGroup("ECOM API - Customer (V2)", "/v3/api-docs/customer-v2"),
        createApiGroup("ECOM API - Contract (V2)", "/v3/api-docs/contract-v2"),
        createApiGroup("ECOM API - Rates", "/v3/api-docs/rates"),
        createApiGroup("ECOM API - Summary", "/v3/api-docs/summary"),
        createApiGroup("ECOM API - Registration", "/v3/api-docs/registration"),
        createApiGroup("ECOM API - Customer Private", "/v3/api-docs/customer-private"),
        createApiGroup("ECOM API - Customer Gate", "/v3/api-docs/customer-gate")
    );

    Map<String, Object> result = new HashMap<>();
    result.put("urls", urls);
    result.put("validatorUrl", "");
    return result;
  }
}
