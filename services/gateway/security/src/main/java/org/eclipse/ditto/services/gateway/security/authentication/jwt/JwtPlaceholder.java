/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.gateway.security.authentication.jwt;

import static org.eclipse.ditto.model.base.common.ConditionChecker.argumentNotEmpty;
import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.util.List;
import java.util.Optional;

import org.eclipse.ditto.json.JsonValue;
import org.eclipse.ditto.model.jwt.JsonWebToken;
import org.eclipse.ditto.model.placeholders.Placeholder;

/**
 * The placeholder that replaces {@code jwt:<body-claim>}.
 */
public final class JwtPlaceholder implements Placeholder<JsonWebToken> {

    private static final JwtPlaceholder INSTANCE = new JwtPlaceholder();

    private static final String PREFIX = "jwt";

    /**
     * Get the instance of {@code JwtPlaceholder}.
     *
     * @return the instance.
     */
    public static JwtPlaceholder getInstance() {
        return INSTANCE;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public List<String> getSupportedNames() {
        return List.of();
    }

    @Override
    public boolean supports(final String name) {
        return true;
    }

    @Override
    public Optional<String> resolve(final JsonWebToken jwt, final String placeholder) {
        argumentNotEmpty(placeholder, "placeholder");
        checkNotNull(jwt, "jwt");
        return jwt.getBody().getValue(placeholder).map(JsonValue::formatAsString);
    }

}
