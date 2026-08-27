package com.santiGalarza.order_management;

import com.santiGalarza.order_management.security.token.TokenHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TokenHasherTest {

    private TokenHasher tokenHasher;

    @BeforeEach
    void setUp(){
        tokenHasher = new TokenHasher();
    }

    @Nested
    @DisplayName("hash")
    class hash{

        @Test
        @DisplayName("produces the same hash for the same input")
        void isDeterministic(){
            String raw = "some-refresh-token-value";

            assertThat(tokenHasher.hash(raw)).isEqualTo(tokenHasher.hash(raw));
        }

        @Test
        @DisplayName("produces different hashes for different inputs")
        void differsForDifferentInputs(){
            String hashA = tokenHasher.hash("hash-a");
            String hashB = tokenHasher.hash("hash-b");

            assertThat(hashA).isNotEqualTo(hashB);
        }

        @Test
        @DisplayName("does not return the raw input")
        void doesNotReturnTheRawInput(){
            String raw = "some-refresh-token-value";

            assertThat(tokenHasher.hash(raw)).isNotEqualTo(raw);
        }

        @Test
        @DisplayName("returns a base64 encoded  SHA 256 digest")
        void returnsBase64EncodedSha256Digest(){
            String hash = tokenHasher.hash("some-refresh-token-value");

            assertThat(hash).matches("^[A-Za-z0-9+/]+=*$");
            assertThat(java.util.Base64.getDecoder().decode(hash)).hasSize(32);
        }
    }
}
