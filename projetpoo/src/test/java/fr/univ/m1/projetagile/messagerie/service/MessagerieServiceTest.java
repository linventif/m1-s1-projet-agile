package fr.univ.m1.projetagile.messagerie.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import fr.univ.m1.projetagile.core.entity.AgentParticulier;
import fr.univ.m1.projetagile.core.entity.Loueur;
import fr.univ.m1.projetagile.messagerie.entity.Message;
import fr.univ.m1.projetagile.messagerie.persistence.MessageRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessagerieService Tests")
class MessagerieServiceTest {

  @Mock
  private MessageRepository messageRepository;

  private MessagerieService service;
  private Loueur loueur;
  private AgentParticulier agent;

  @BeforeEach
  void setUp() {
    service = new MessagerieService(messageRepository);

    loueur = new Loueur("Doe", "John", "john@example.com", "pass");
    loueur.setIdU(1L);

    agent = new AgentParticulier("Smith", "Alice", "alice@example.com", "pass", "0612345678");
    agent.setIdU(2L);
  }

  @Nested
  @DisplayName("EnvoyerMessage Tests")
  class EnvoyerMessageTests {

    @Test
    @DisplayName("Should send valid message successfully")
    void shouldSendValidMessageSuccessfully() {
      // Given
      String contenu = "Test message";
      Message expectedMessage = new Message(contenu, loueur, agent);
      expectedMessage.setId(100L);

      when(messageRepository.save(any(Message.class))).thenReturn(expectedMessage);

      // When
      Message result = service.envoyerMessage(loueur, agent, contenu);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(100L);
      assertThat(result.getContenu()).isEqualTo(contenu);
      verify(messageRepository).save(any(Message.class));
    }

    @Test
    @DisplayName("Should throw exception when expediteur is null")
    void shouldThrowExceptionWhenExpediteurIsNull() {
      // When/Then
      assertThatThrownBy(() -> service.envoyerMessage(null, agent, "Test"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("L'expéditeur ne peut pas être null");

      verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when destinataire is null")
    void shouldThrowExceptionWhenDestinataireIsNull() {
      // When/Then
      assertThatThrownBy(() -> service.envoyerMessage(loueur, null, "Test"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Le destinataire ne peut pas être null");

      verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when expediteur has no ID")
    void shouldThrowExceptionWhenExpediteurHasNoId() {
      // Given
      Loueur newLoueur = new Loueur("New", "User", "new@test.com", "pass");
      // No ID set

      // When/Then
      assertThatThrownBy(() -> service.envoyerMessage(newLoueur, agent, "Test"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("L'expéditeur doit être enregistré en base de données");

      verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when destinataire has no ID")
    void shouldThrowExceptionWhenDestinataireHasNoId() {
      // Given
      AgentParticulier newAgent =
          new AgentParticulier("New", "Agent", "new@test.com", "pass", "0601");
      // No ID set

      // When/Then
      assertThatThrownBy(() -> service.envoyerMessage(loueur, newAgent, "Test"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Le destinataire doit être enregistré en base de données");

      verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when content is empty")
    void shouldThrowExceptionWhenContentIsEmpty() {
      // When/Then
      assertThatThrownBy(() -> service.envoyerMessage(loueur, agent, ""))
          .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("n'est pas valide");

      verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when content is too long")
    void shouldThrowExceptionWhenContentIsTooLong() {
      // Given
      String longContent = "a".repeat(1001);

      // When/Then
      assertThatThrownBy(() -> service.envoyerMessage(loueur, agent, longContent))
          .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("n'est pas valide");

      verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should accept message at max length")
    void shouldAcceptMessageAtMaxLength() {
      // Given
      String maxContent = "a".repeat(1000);
      Message expectedMessage = new Message(maxContent, loueur, agent);
      expectedMessage.setId(100L);

      when(messageRepository.save(any(Message.class))).thenReturn(expectedMessage);

      // When
      Message result = service.envoyerMessage(loueur, agent, maxContent);

      // Then
      assertThat(result).isNotNull();
      verify(messageRepository).save(any(Message.class));
    }
  }

  @Nested
  @DisplayName("GetMessagesUtilisateur Tests")
  class GetMessagesUtilisateurTests {

    @Test
    @DisplayName("Should retrieve all user messages")
    void shouldRetrieveAllUserMessages() {
      // Given
      Message msg1 = new Message("Message 1", loueur, agent);
      Message msg2 = new Message("Message 2", agent, loueur);
      List<Message> expected = Arrays.asList(msg1, msg2);

      when(messageRepository.findAllMessagesByUser(loueur)).thenReturn(expected);

      // When
      List<Message> result = service.getMessagesUtilisateur(loueur);

      // Then
      assertThat(result).hasSize(2);
      assertThat(result).containsExactlyElementsOf(expected);
      verify(messageRepository).findAllMessagesByUser(loueur);
    }

    @Test
    @DisplayName("Should throw exception when utilisateur is null")
    void shouldThrowExceptionWhenUtilisateurIsNull() {
      // When/Then
      assertThatThrownBy(() -> service.getMessagesUtilisateur(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("L'utilisateur ne peut pas être null");

      verify(messageRepository, never()).findAllMessagesByUser(any());
    }
  }

  @Nested
  @DisplayName("GetMessagesEnvoyes Tests")
  class GetMessagesEnvoyesTests {

    @Test
    @DisplayName("Should retrieve sent messages")
    void shouldRetrieveSentMessages() {
      // Given
      Message msg1 = new Message("Sent 1", loueur, agent);
      Message msg2 = new Message("Sent 2", loueur, agent);
      List<Message> expected = Arrays.asList(msg1, msg2);

      when(messageRepository.findMessagesSentBy(loueur)).thenReturn(expected);

      // When
      List<Message> result = service.getMessagesEnvoyes(loueur);

      // Then
      assertThat(result).hasSize(2);
      assertThat(result).containsExactlyElementsOf(expected);
      verify(messageRepository).findMessagesSentBy(loueur);
    }

    @Test
    @DisplayName("Should throw exception when utilisateur is null")
    void shouldThrowExceptionWhenUtilisateurIsNull() {
      // When/Then
      assertThatThrownBy(() -> service.getMessagesEnvoyes(null))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  @DisplayName("GetMessagesRecus Tests")
  class GetMessagesRecusTests {

    @Test
    @DisplayName("Should retrieve received messages")
    void shouldRetrieveReceivedMessages() {
      // Given
      Message msg1 = new Message("Received 1", agent, loueur);
      Message msg2 = new Message("Received 2", agent, loueur);
      List<Message> expected = Arrays.asList(msg1, msg2);

      when(messageRepository.findMessagesReceivedBy(loueur)).thenReturn(expected);

      // When
      List<Message> result = service.getMessagesRecus(loueur);

      // Then
      assertThat(result).hasSize(2);
      assertThat(result).containsExactlyElementsOf(expected);
      verify(messageRepository).findMessagesReceivedBy(loueur);
    }
  }

  @Nested
  @DisplayName("GetConversation Tests")
  class GetConversationTests {

    @Test
    @DisplayName("Should retrieve conversation between two users")
    void shouldRetrieveConversationBetweenTwoUsers() {
      // Given
      Message msg1 = new Message("Hello", loueur, agent);
      Message msg2 = new Message("Hi", agent, loueur);
      List<Message> expected = Arrays.asList(msg1, msg2);

      when(messageRepository.findConversationBetween(loueur, agent)).thenReturn(expected);

      // When
      List<Message> result = service.getConversation(loueur, agent);

      // Then
      assertThat(result).hasSize(2);
      assertThat(result).containsExactlyElementsOf(expected);
      verify(messageRepository).findConversationBetween(loueur, agent);
    }

    @Test
    @DisplayName("Should throw exception when one user is null")
    void shouldThrowExceptionWhenOneUserIsNull() {
      // When/Then
      assertThatThrownBy(() -> service.getConversation(null, agent))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Les deux utilisateurs doivent être non null");

      assertThatThrownBy(() -> service.getConversation(loueur, null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Les deux utilisateurs doivent être non null");
    }
  }

  @Nested
  @DisplayName("GetMessageById Tests")
  class GetMessageByIdTests {

    @Test
    @DisplayName("Should retrieve message by ID")
    void shouldRetrieveMessageById() {
      // Given
      Message expected = new Message("Test", loueur, agent);
      expected.setId(100L);

      when(messageRepository.findById(100L)).thenReturn(expected);

      // When
      Message result = service.getMessageById(100L);

      // Then
      assertThat(result).isEqualTo(expected);
      verify(messageRepository).findById(100L);
    }

    @Test
    @DisplayName("Should throw exception when ID is null")
    void shouldThrowExceptionWhenIdIsNull() {
      // When/Then
      assertThatThrownBy(() -> service.getMessageById(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("L'ID du message ne peut pas être null");
    }
  }

  @Nested
  @DisplayName("SupprimerMessage Tests")
  class SupprimerMessageTests {

    @Test
    @DisplayName("Should delete message by ID")
    void shouldDeleteMessageById() {
      // When
      service.supprimerMessage(100L);

      // Then
      verify(messageRepository).delete(100L);
    }

    @Test
    @DisplayName("Should throw exception when ID is null")
    void shouldThrowExceptionWhenIdIsNull() {
      // When/Then
      assertThatThrownBy(() -> service.supprimerMessage(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("L'ID du message ne peut pas être null");

      verify(messageRepository, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("Utility Methods Tests")
  class UtilityMethodsTests {

    @Test
    @DisplayName("Should count messages in conversation")
    void shouldCountMessagesInConversation() {
      // Given
      List<Message> messages = Arrays.asList(new Message("1", loueur, agent),
          new Message("2", agent, loueur), new Message("3", loueur, agent));

      when(messageRepository.findConversationBetween(loueur, agent)).thenReturn(messages);

      // When
      int count = service.compterMessagesConversation(loueur, agent);

      // Then
      assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should count unread messages")
    void shouldCountUnreadMessages() {
      // Given
      List<Message> received =
          Arrays.asList(new Message("1", agent, loueur), new Message("2", agent, loueur));

      when(messageRepository.findMessagesReceivedBy(loueur)).thenReturn(received);

      // When
      int count = service.compterMessagesNonLus(loueur);

      // Then
      assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should verify users have exchanged messages")
    void shouldVerifyUsersHaveExchangedMessages() {
      // Given
      List<Message> messages = Arrays.asList(new Message("Test", loueur, agent));
      when(messageRepository.findConversationBetween(loueur, agent)).thenReturn(messages);

      // When
      boolean result = service.ontEchangeMessages(loueur, agent);

      // Then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should verify users have not exchanged messages")
    void shouldVerifyUsersHaveNotExchangedMessages() {
      // Given
      when(messageRepository.findConversationBetween(loueur, agent)).thenReturn(List.of());

      // When
      boolean result = service.ontEchangeMessages(loueur, agent);

      // Then
      assertThat(result).isFalse();
    }
  }
}
