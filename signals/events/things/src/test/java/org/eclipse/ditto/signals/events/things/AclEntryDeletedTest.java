/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.ditto.signals.events.things;

import static org.eclipse.ditto.json.assertions.DittoJsonAssertions.assertThat;
import static org.mutabilitydetector.unittesting.AllowedReason.provided;
import static org.mutabilitydetector.unittesting.MutabilityAssert.assertInstancesOf;
import static org.mutabilitydetector.unittesting.MutabilityMatchers.areImmutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.model.base.auth.AuthorizationSubject;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.things.ThingId;
import org.eclipse.ditto.model.things.ThingIdInvalidException;
import org.eclipse.ditto.signals.events.base.Event;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Unit test for {@link AclEntryDeleted}.
 */
public final class AclEntryDeletedTest {

    private static final JsonObject KNOWN_JSON = JsonFactory.newObjectBuilder()
            .set(Event.JsonFields.TIMESTAMP, TestConstants.TIMESTAMP.toString())
            .set(Event.JsonFields.ID, AclEntryDeleted.NAME)
            .set(Event.JsonFields.REVISION, 2L)
            .set(Event.JsonFields.METADATA, TestConstants.METADATA.toJson())
            .set(ThingEvent.JsonFields.THING_ID, TestConstants.Thing.THING_ID.toString())
            .set(AclEntryDeleted.JSON_AUTHORIZATION_SUBJECT, TestConstants.Authorization.AUTH_SUBJECT_GRIMES.getId())
            .build();

    @Test
    public void assertImmutability() {
        assertInstancesOf(AclEntryDeleted.class,
                areImmutable(),
                provided(AuthorizationSubject.class).isAlsoImmutable());
    }

    @Test
    public void testHashCodeAndEquals() {
        EqualsVerifier.forClass(AclEntryDeleted.class)
                .withRedefinedSuperclass()
                .verify();
    }

    @Test(expected = ThingIdInvalidException.class)
    public void tryToCreateInstanceWithNullThingIdString() {
        AclEntryDeleted.of((String) null, TestConstants.Authorization.AUTH_SUBJECT_GRIMES, TestConstants.Thing.REVISION_NUMBER,
                TestConstants.EMPTY_DITTO_HEADERS);
    }

    @Test(expected = NullPointerException.class)
    public void tryToCreateInstanceWithNullThingId() {
        AclEntryDeleted.of((ThingId) null, TestConstants.Authorization.AUTH_SUBJECT_GRIMES, TestConstants.Thing.REVISION_NUMBER,
                TestConstants.EMPTY_DITTO_HEADERS);
    }

    @Test(expected = NullPointerException.class)
    public void tryToCreateInstanceWithNullAclSubject() {
        AclEntryDeleted.of(TestConstants.Thing.THING_ID, null, TestConstants.Thing.REVISION_NUMBER,
                TestConstants.EMPTY_DITTO_HEADERS);
    }

    @Test
    public void createInstanceFromValidJson() {
        final AclEntryDeleted underTest =
                AclEntryDeleted.fromJson(KNOWN_JSON.toString(), TestConstants.EMPTY_DITTO_HEADERS);

        assertThat(underTest.getAuthorizationSubject()).isEqualTo(TestConstants.Authorization.AUTH_SUBJECT_GRIMES);
    }

    @Test
    public void toJsonReturnsExpected() {
        final AclEntryDeleted underTest =
                AclEntryDeleted.of(TestConstants.Thing.THING_ID, TestConstants.Authorization.AUTH_SUBJECT_GRIMES,
                        2, TestConstants.TIMESTAMP, TestConstants.EMPTY_DITTO_HEADERS, TestConstants.METADATA);
        final JsonObject actualJson = underTest.toJson(JsonSchemaVersion.V_1, FieldType.regularOrSpecial());

        assertThat(actualJson).isEqualToIgnoringFieldDefinitions(KNOWN_JSON
                .remove(Event.JsonFields.ID.getPointer())
                .set(Event.JsonFields.TYPE, AclEntryDeleted.TYPE));
    }

}
