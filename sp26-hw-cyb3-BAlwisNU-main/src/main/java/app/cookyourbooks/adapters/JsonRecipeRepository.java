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

import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.repository.RecipeRepository;
import app.cookyourbooks.repository.RepositoryException;

/**
 * JSON file-backed implementation of {@link RecipeRepository}.
 *
 * <p><strong>Storage Model:</strong> Each recipe is stored as a single JSON file named {@code
 * {id}.json} within a directory provided at construction time.
 *
 * <p><strong>Polymorphism:</strong> Polymorphic types within {@link Recipe} (e.g., {@code Quantity}
 * and {@code Ingredient}) are handled by Jackson via domain annotations already present in the
 * starter code.
 *
 * <p><strong>Error Handling:</strong> All I/O and serialization failures are wrapped in {@link
 * RepositoryException}. Encountering corrupt JSON during reads also results in {@link
 * RepositoryException} (per spec).
 */
public class JsonRecipeRepository implements RecipeRepository {

  /**
   * Pre-configured ObjectMapper for JSON serialization.
   *
   * <p>Includes:
   *
   * <ul>
   *   <li>{@link Jdk8Module} for {@code Optional} support
   *   <li>{@link JavaTimeModule} for Java 8 date/time support
   * </ul>
   */
  protected final ObjectMapper objectMapper;

  protected final Path storageDirectory;

  /**
   * Constructs a new JSON recipe repository.
   *
   * <p>Creates the storage directory if it doesn't exist.
   *
   * @param storageDirectory the directory where recipe JSON files will be stored
   * @throws app.cookyourbooks.repository.RepositoryException if the directory cannot be created, if
   *     the path exists but is not a directory, or if the directory is not accessible
   */
  public JsonRecipeRepository(Path storageDirectory) {
    this.objectMapper = createObjectMapper();
    this.storageDirectory = Objects.requireNonNull(storageDirectory);

    try {
      if (Files.exists(storageDirectory) && !Files.isDirectory(storageDirectory)) {
        throw new RepositoryException("Path exists but is not a directory: " + storageDirectory);
      }
      Files.createDirectories(storageDirectory);
    } catch (IOException e) {
      throw new RepositoryException(
          "Failed to initialize recipe repository at " + storageDirectory, e);
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
   * Returns the JSON file path for a given recipe ID.
   *
   * @param id recipe ID (must not be null)
   * @return path to {@code {id}.json} (never null)
   */
  private Path fileForId(String id) {
    return Objects.requireNonNull(storageDirectory.resolve(id + ".json"));
  }

  /**
   * Saves a recipe to the repository.
   *
   * <p>If a recipe with the same ID already exists, it is replaced.
   *
   * @param recipe the recipe to save (must not be null)
   * @throws RepositoryException if the save operation fails
   */
  @Override
  public void save(Recipe recipe) {
    try {
      objectMapper.writeValue(fileForId(recipe.getId()).toFile(), recipe);
    } catch (IOException e) {
      throw new RepositoryException("Failed to save recipe " + recipe.getId(), e);
    }
  }

  /**
   * Finds a recipe by ID.
   *
   * @param id recipe ID (must not be null)
   * @return the recipe if found, otherwise empty
   * @throws RepositoryException if the read fails or JSON is corrupt
   */
  @Override
  public Optional<Recipe> findById(String id) {
    Path file = fileForId(id);
    if (!Files.exists(file)) {
      return Optional.<@NonNull Recipe>empty();
    }

    try {
      Recipe recipe =
          Objects.requireNonNull(
              objectMapper.readValue(file.toFile(), Recipe.class), "Recipe JSON must not be null");
      return Objects.requireNonNull(Optional.of(recipe));
    } catch (IOException e) {
      throw new RepositoryException("Failed to read recipe " + id, e);
    }
  }

  /**
   * Finds any recipe matching the given title using case-insensitive exact match.
   *
   * <p>If multiple recipes match, which one is returned is implementation-defined.
   *
   * @param title title to search for (must not be null)
   * @return any one matching recipe, or empty if none match
   * @throws RepositoryException if repository access fails
   */
  @Override
  public Optional<Recipe> findByTitle(String title) {
    return Objects.requireNonNull(
        findAll().stream().filter(r -> r.getTitle().equalsIgnoreCase(title)).findFirst());
  }

  /**
   * Finds all recipes matching the given title using case-insensitive exact match.
   *
   * <p>The order of results is unspecified.
   *
   * @param title title to search for (must not be null)
   * @return all matching recipes (never null)
   * @throws RepositoryException if repository access fails
   */
  @Override
  public List<Recipe> findAllByTitle(String title) {
    return Objects.requireNonNull(
        findAll().stream().filter(r -> r.getTitle().equalsIgnoreCase(title)).toList());
  }

  /**
   * Reads a single recipe file from disk.
   *
   * @param file the JSON file path (must not be null)
   * @return the deserialized recipe
   * @throws RepositoryException if the file cannot be read or JSON is invalid
   */
  private Recipe readRecipeFile(Path file) {
    try {
      return Objects.requireNonNull(
          objectMapper.readValue(file.toFile(), Recipe.class), "Recipe JSON must not be null");
    } catch (IOException e) {
      throw new RepositoryException("Failed to read recipe file " + file, e);
    }
  }

  /**
   * Returns all recipes in the repository.
   *
   * @return all recipes (never null, may be empty)
   * @throws RepositoryException if listing fails or any JSON file is corrupt
   */
  @Override
  public List<Recipe> findAll() {
    try (var stream = Files.list(storageDirectory)) {
      return stream
          .filter(p -> p.getFileName().toString().endsWith(".json"))
          .map(path -> readRecipeFile(Objects.requireNonNull(path, "path must not be null")))
          .toList();
    } catch (IOException e) {
      throw new RepositoryException("Failed to list recipes in " + storageDirectory, e);
    }
  }

  /**
   * Deletes a recipe by ID.
   *
   * <p>This operation is idempotent: if the file does not exist, this method does nothing.
   *
   * @param id recipe ID (must not be null)
   * @throws RepositoryException if deletion fails
   */
  @Override
  public void delete(String id) {
    Path file = fileForId(id);
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      throw new RepositoryException("Failed to delete recipe " + id, e);
    }
  }
}
