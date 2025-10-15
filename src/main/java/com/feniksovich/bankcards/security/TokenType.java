package com.feniksovich.bankcards.security;

public enum TokenType {

    ACCESS, REFRESH;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
