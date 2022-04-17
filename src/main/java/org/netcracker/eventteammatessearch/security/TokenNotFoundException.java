package org.netcracker.eventteammatessearch.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, value = HttpStatus.NOT_FOUND, reason = "jwt token is invalid")
public class TokenNotFoundException extends Exception {
}
