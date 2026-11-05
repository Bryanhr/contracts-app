package pe.edu.solweb.contracts.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContractTest {

    private static final LocalDate START = LocalDate.of(2026, 11, 1);
    private static final LocalDate END = LocalDate.of(2027, 11, 1);

    private static Contract contract(ContractStatus status) {
        Contract c = new Contract();
        c.setStatus(status);
        c.setStartDate(START);
        c.setEndDate(END);
        return c;
    }

    @Test
    void unsignedContractHasNoValidity() {
        assertNull(contract(ContractStatus.GENERATED).validityOn(START.plusDays(10)));
    }

    @Test
    void signedContractValidityFollowsItsDates() {
        Contract c = contract(ContractStatus.SIGNED);
        assertEquals(Validity.PENDING, c.validityOn(START.minusDays(1)));
        assertEquals(Validity.ACTIVE, c.validityOn(START));
        assertEquals(Validity.ACTIVE, c.validityOn(END.minusDays(31)));
        assertEquals(Validity.EXPIRING, c.validityOn(END.minusDays(30)));
        assertEquals(Validity.EXPIRING, c.validityOn(END));
        assertEquals(Validity.EXPIRED, c.validityOn(END.plusDays(1)));
    }
}
