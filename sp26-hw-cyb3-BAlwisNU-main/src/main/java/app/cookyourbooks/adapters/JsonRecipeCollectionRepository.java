package app.cookyourbooks.adapters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import app.cookyourbooks.model.RecipeCollection;
import app.cookyourbooks.repository.RecipeCollectionRepository;
import app.cookyourbooks.repository.RepositoryException;

/**
 * JSON file-based implementation of {@link RecipeCollectionRepository}.
 *
 * <p>This repository stores each recipe collection as a separate JSON file in the specified storage
 * directory. The filename is based on the collection's unique identifier.
 *
 * <p><strong>Polymorphism:</strong> This repository preserves the concrete type of collections.
 * Saving a {@link app.cookyourbooks.model.Cookbook} and loading it back returns a {@code Cookbook},
 * not just a {@code RecipeCollection}. You must configure Jackson's polymorphic type handling for
 * your collection implementations.
 *
 * <p><strong>Implementation Notes:</strong>
 *
 * <ul>
 *   <li>Each collection is stored as {@code {id}.json} in the storage directory
 *   <li>The {@link ObjectMapper} is pre-configured with modules for {@code Optional} and Java 8
 *       date/time support
 *   <li>You must add {@code @JsonTypeInfo} and {@code @JsonSubTypes} annotations to your collection
 *       implementations (similar to how {@code Quantity} and {@code Ingredient} are configured)
 *   <li>Nested recipes within collections are serialized inline—design decision is yours whether to
 *       embed or reference
 * </ul>
 *
 * @see RecipeCollectionRepository
 */
public class JsonRecipeCollectionRepository implements RecipeCollectionRepository {

  private final Path storageDirectory;

  /**
   * Pre-configured ObjectMapper for JSON serialization.
   *
   * <p>Includes:
   *
   * <ul>
   *   <li>{@link Jdk8Module} for {@code Optional} support
   *   <li>{@link JavaTimeModule} for Java 8 date/time support (e.g., {@code LocalDate})
   * </ul>
   *
   * <p><strong>Note:</strong> You may need to configure additional settings for your collection
   * class polymorphism. See the assignment writeup for guidance on {@code @JsonTypeInfo} and
   * {@code @JsonSubTypes}.
   */
  private final ObjectMapper objectMapper;

  /**
   * Constructs a new JSON recipe collection repository.
   *
   * <p>Creates the storage directory if it doesn't exist.
   *
   * @param storageDirectory the directory where collection JSON files will be stored
   * @throws app.cookyourbooks.repository.RepositoryException if the directory cannot be created, if
   *     the path exists but is not a directory, or if the directory is not accessible
   */
  public JsonRecipeCollectionRepository(Path storageDirectory) {
    this.storageDirectory = storageDirectory;
    this.objectMapper = createObjectMapper();

    try {
      if (Files.exists(this.storageDirectory)) {
        if (!Files.isDirectory(this.storageDirectory)) {
          throw new RepositoryException(
              "Path exists but is not a directory: " + this.storageDirectory);
        }
      } else {
        Files.createDirectories(this.storageDirectory);
      }
    } catch (IOException e) {
      throw new RepositoryException(
          "Failed to initialize storage directory: " + this.storageDirectory, e);
    }
  }

  /**
   * Creates and configures the ObjectMapper for JSON serialization.
   *
   * @return a configured ObjectMapper instance
   */
  private static ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  /**
   * Returns the JSON file path for a given collection ID.
   *
   * @param id collection ID
   * @return path to {@code {id}.json}
   */
  private Path fileForId(String id) {
    return Objects.requireNonNull(storageDirectory.resolve(id + ".json"));
  }

  /**
   * Saves a collection to the repository.
   *
   * <p>If a collection with the same ID already exists, it is replaced.
   *
   * @param collection the collection to save (must not be null)
   * @throws RepositoryException if the save fails
   */
  @Override
  public void save(RecipeCollection collection) {
    try {
      objectMapper.writeValue(fileForId(collection.getId()).toFile(), collection);
    } catch (IOException e) {
      throw new RepositoryException("Failed to save collection " + collection.getId(), e);
    }
  }

  /**
   * Finds a collection by ID.
   *
   * <p>The returned instance preserves its concrete type (Cookbook, PersonalCollection,
   * WebCollection).
   *
   * @param id collection ID (must not be null)
   * @return the collection if found, otherwise empty
   * @throws RepositoryException if the read fails or JSON is corrupt
   */
  @Override
  public Optional<RecipeCollection> findById(String id) {
    Path file = fileForId(id);
    if (!Files.exists(file)) {
      return Optional.<@NonNull RecipeCollection>empty();
    }
    try {
      RecipeCollection collection =
          Objects.requireNonNull(
              objectMapper.readValue(file.toFile(), RecipeCollection.class),
              "Collection JSON must not be null");
      return Objects.requireNonNull(Optional.of(collection));
    } catch (IOException e) {
      throw new RepositoryException("Failed to read collection " + id, e);
    }
  }

  /**
   * Finds any collection matching the given title using case-insensitive exact match.
   *
   * <p>If multiple collections match, which one is returned is implementation-defined.
   *
   * @param title title to search for (must not be null)
   * @return any one matching collection, or empty if none match
   * @throws RepositoryException if repository access fails
   */
  @Override
  public Optional<RecipeCollection> findByTitle(String title) {
    return Objects.requireNonNull(
        findAll().stream().filter(c -> c.getTitle().equalsIgnoreCase(title)).findFirst());
  }

  /**
   * Finds all collections matching the given title using case-insensitive exact match.
   *
   * <p>The order of results is unspecified.
   *
   * @param title title to search for (must not be null)
   * @return all matching collections (never null)
   * @throws RepositoryException if repository access fails
   */
  @Override
  public List<RecipeCollection> findAllByTitle(String title) {
    return Objects.requireNonNull(
        findAll().stream().filter(c -> c.getTitle().equalsIgnoreCase(title)).toList());
  }

  /**
   * Returns all collections in the repository.
   *
   * <p>The order of results is unspecified.
   *
   * <p><strong>Corrupt JSON:</strong> If any JSON file in the directory is corrupt or cannot be
   * deserialized, this method throws {@link RepositoryException}.
   *
   * @return all collections (never null)
   * @throws RepositoryException if listing or reading fails
   */
  @Override
  public List<RecipeCollection> findAll() {
    try (var stream = Files.list(storageDirectory)) {
      List<Path> jsonFiles =
          stream.filter(p -> p.getFileName().toString().endsWith(".json")).toList();

      return jsonFiles.stream()
          .map(
              p -> {
                try {
                  return Objects.requireNonNull(
                      objectMapper.readValue(p.toFile(), RecipeCollection.class),
                      "Collection JSON must not be null");
                } catch (IOException e) {
                  throw new RepositoryException("Failed to read collection file " + p, e);
                }
              })
          .toList();
    } catch (IOException e) {
      throw new RepositoryException("Failed to list collections in " + storageDirectory, e);
    }
  }

  /**
   * Deletes a collection by ID.
   *
   * <p>This operation is idempotent: if the file does not exist, this method does nothing.
   *
   * @param id collection ID (must not be null)
   * @throws RepositoryException if deletion fails
   */
  @Override
  public void delete(String id) {
    Path file = fileForId(id);
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new RepositoryException("Failed to delete collection " + id, e);
    }
  }
}
