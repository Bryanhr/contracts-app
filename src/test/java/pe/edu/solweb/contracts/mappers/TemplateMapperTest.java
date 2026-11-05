package pe.edu.solweb.contracts.mappers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateMapperTest {

    @Test
    void addsTheDocumentHeadAndTailAroundAPlanClause() {
        String stored = TemplateMapper.document(
                "Se acuerda la prestación de soporte técnico por {{duracion}}.");

        assertTrue(stored.startsWith("# CONTRATO DE PRESTACIÓN DE SERVICIOS DE SOPORTE"));
        assertTrue(stored.contains("N.º {{numero}}"));
        assertTrue(stored.contains("{{servicios}}\nSe acuerda la prestación de soporte técnico"));
        assertTrue(stored.contains("## 2) Vigencia y tiempo de atención."));
        assertTrue(stored.endsWith("{{firmado}}"));
        assertEquals(1, count(stored, "# CONTRATO"));
    }

    @Test
    void keepsATemplateThatAlreadyHasTheDocumentHead() {
        String complete = """
                # CONTRATO DE PRESTACIÓN DE SERVICIOS DE SOPORTE

                N.º {{numero}}
                ## 1) Servicios del plan.
                {{servicios}}
                Cláusula propia.
                ## 2) Vigencia y tiempo de atención.
                """;

        assertEquals(complete.strip(), TemplateMapper.document(complete));
        assertFalse(TemplateMapper.document(complete).contains("Cláusula propia.\n# CONTRATO"));
    }

    private static int count(String text, String piece) {
        int found = 0;
        int from = 0;
        while ((from = text.indexOf(piece, from)) >= 0) {
            found++;
            from += piece.length();
        }
        return found;
    }
}
