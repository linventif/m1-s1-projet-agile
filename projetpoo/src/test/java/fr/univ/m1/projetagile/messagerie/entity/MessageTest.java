package fr.univ.m1.projetagile.messagerie.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.core.entity.Utilisateur;

@DisplayName("Message Entity Tests")
class MessageTest {

  private Loueur expediteur;
  private AgentParticulier destinataire;

  @BeforeEach
  void setUp() {
    // Créer des utilisateurs avec des IDs
    expediteur = new Loueur("Doe", "John", "john.doe@example.com", "password123");
    expediteur.setIdU(1L);

    destinataire = new AgentParticulier("Smith", "Alice", "alice.smith@example.com", "password456",
        "0612345678");
    destinataire.setIdU(2L);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should create message with valid parameters")
    void shouldCreateMessageWithValidParameters() {
      // When
      Message message = new Message("Test message", expediteur, destinataire);

      // Then
      assertThat(message.getContenu()).isEqualTo("Test message");
      assertThat(message.getExpediteurId()).isEqualTo(1L);
      assertThat(message.getDestinataireId()).isEqualTo(2L);
      assertThat(message.getDateEnvoi()).isNotNull();
      assertThat(message.getDateEnvoi()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create message with current timestamp")
    void shouldCreateMessageWithCurrentTimestamp() {
      // Given
      LocalDateTime before = LocalDateTime.now();

      // When
      Message message = new Message("Test", expediteur, destinataire);

      // Then
      LocalDateTime after = LocalDateTime.now();
      assertThat(message.getDateEnvoi()).isBetween(before, after);
    }

    @Test
    @DisplayName("Should throw exception when expediteur is null")
    void shouldThrowExceptionWhenExpediteurIsNull() {
      // When/Then
      assertThatThrownBy(() -> new Message("Test", null, destinataire))
          .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw exception when destinataire is null")
    void shouldThrowExceptionWhenDestinataireIsNull() {
      // When/Then
      assertThatThrownBy(() -> new Message("Test", expediteur, null))
          .isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  @DisplayName("Content Validation Tests")
  class ContentValidationTests {

    @Test
    @DisplayName("Should validate correct content")
    void shouldValidateCorrectContent() {
      // Given
      Message message = new Message("Valid content", expediteur, destinataire);

      // When/Then
      assertThat(message.verifierContenu()).isTrue();
    }

    @Test
    @DisplayName("Should reject null content")
    void shouldRejectNullContent() {
      // Given
      Message message = new Message(null, expediteur, destinataire);

      // When/Then
      assertThat(message.verifierContenu()).isFalse();
    }

    @Test
    @DisplayName("Should reject empty content")
    void shouldRejectEmptyContent() {
      // Given
      Message message = new Message("", expediteur, destinataire);

      // When/Then
      assertThat(message.verifierContenu()).isFalse();
    }

    @Test
    @DisplayName("Should reject whitespace-only content")
    void shouldRejectWhitespaceOnlyContent() {
      // Given
      Message message = new Message("   ", expediteur, destinataire);

      // When/Then
      assertThat(message.verifierContenu()).isFalse();
    }

    @Test
    @DisplayName("Should reject content exceeding max length")
    void shouldRejectContentExceedingMaxLength() {
      // Given
      String longContent = "a".repeat(1001);
      Message message = new Message(longContent, expediteur, destinataire);

      // When/Then
      assertThat(message.verifierContenu()).isFalse();
    }

    @Test
    @DisplayName("Should accept content at max length")
    void shouldAcceptContentAtMaxLength() {
      // Given
      String maxContent = "a".repeat(1000);
      Message message = new Message(maxContent, expediteur, destinataire);

      // When/Then
      assertThat(message.verifierContenu()).isTrue();
    }
  }

  @Nested
  @DisplayName("Getter/Setter Tests")
  class GetterSetterTests {

    @Test
    @DisplayName("Should get and set ID")
    void shouldGetAndSetId() {
      // Given
      Message message = new Message("Test", expediteur, destinataire);

      // When
      message.setId(100L);

      // Then
      assertThat(message.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should get and set contenu")
    void shouldGetAndSetContenu() {
      // Given
      Message message = new Message("Original", expediteur, destinataire);

      // When
      message.setContenu("Updated content");

      // Then
      assertThat(message.getContenu()).isEqualTo("Updated content");
    }

    @Test
    @DisplayName("Should get and set dateEnvoi")
    void shouldGetAndSetDateEnvoi() {
      // Given
      Message message = new Message("Test", expediteur, destinataire);
      LocalDateTime newDate = LocalDateTime.of(2026, 1, 1, 12, 0);

      // When
      message.setDateEnvoi(newDate);

      // Then
      assertThat(message.getDateEnvoi()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("Should get expediteur ID")
    void shouldGetExpediteurId() {
      // Given
      Message message = new Message("Test", expediteur, destinataire);

      // When/Then
      assertThat(message.getExpediteurId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get destinataire ID")
    void shouldGetDestinataireId() {
      // Given
      Message message = new Message("Test", expediteur, destinataire);

      // When/Then
      assertThat(message.getDestinataireId()).isEqualTo(2L);
    }
  }

  @Nested
  @DisplayName("ToString Tests")
  class ToStringTests {

    @Test
    @DisplayName("Should generate toString with all fields")
    void shouldGenerateToStringWithAllFields() {
      // Given
      Message message = new Message("Test message", expediteur, destinataire);
      message.setId(42L);

      // When
      String result = message.toString();

      // Then
      assertThat(result).contains("id=42");
      assertThat(result).contains("Test message");
      assertThat(result).contains("expediteurId=1");
      assertThat(result).contains("destinataireId=2");
    }
  }

  @Nested
  @DisplayName("Business Logic Tests")
  class BusinessLogicTests {

    @Test
    @DisplayName("Should store correct user IDs from polymorphic users")
    void shouldStoreCorrectUserIdsFromPolymorphicUsers() {
      // Given
      Utilisateur expediteurAsUtilisateur = expediteur;
      Utilisateur destinataireAsUtilisateur = destinataire;

      // When
      Message message = new Message("Test", expediteurAsUtilisateur, destinataireAsUtilisateur);

      // Then
      assertThat(message.getExpediteurId()).isEqualTo(expediteur.getIdU());
      assertThat(message.getDestinataireId()).isEqualTo(destinataire.getIdU());
    }

    @Test
    @DisplayName("Should handle message between two agents")
    void shouldHandleMessageBetweenTwoAgents() {
      // Given
      AgentParticulier agent1 =
          new AgentParticulier("Agent1", "Test", "agent1@test.com", "pass", "0601");
      agent1.setIdU(10L);
      AgentParticulier agent2 =
          new AgentParticulier("Agent2", "Test", "agent2@test.com", "pass", "0602");
      agent2.setIdU(20L);

      // When
      Message message = new Message("Message between agents", agent1, agent2);

      // Then
      assertThat(message.getExpediteurId()).isEqualTo(10L);
      assertThat(message.getDestinataireId()).isEqualTo(20L);
    }

    @Test
    @DisplayName("Should handle message between two loueurs")
    void shouldHandleMessageBetweenTwoLoueurs() {
      // Given
      Loueur loueur1 = new Loueur("Loueur1", "Test", "loueur1@test.com", "pass");
      loueur1.setIdU(30L);
      Loueur loueur2 = new Loueur("Loueur2", "Test", "loueur2@test.com", "pass");
      loueur2.setIdU(40L);

      // When
      Message message = new Message("Message between loueurs", loueur1, loueur2);

      // Then
      assertThat(message.getExpediteurId()).isEqualTo(30L);
      assertThat(message.getDestinataireId()).isEqualTo(40L);
    }
  }
}
