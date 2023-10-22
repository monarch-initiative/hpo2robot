package org.monarchinitiative.controller;

public enum EmailLoginResult {

    SUCCESS,
    FAILED_BY_CREDENTIALS,
    FAILED_BY_NETWORK,

    UNEXPECTED_ERROR;
}
