package com.ventaslibros.application.dto.response;

import java.math.BigDecimal;

public class SaldoResponse {
    private BigDecimal saldo;

    public SaldoResponse(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}