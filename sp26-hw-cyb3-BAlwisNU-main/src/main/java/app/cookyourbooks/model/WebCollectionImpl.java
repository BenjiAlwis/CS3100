package app.cookyourbooks.model;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable implementation of {@link WebCollection}.
 *
 * <p><strong>Immutability:</strong> This class is immutable. The {@link #addRecipe(Recipe)} and
 * {@link #removeRecipe(String)} methods return new instances rather than modifying this instance.
 *
 * <p><strong>Blank String Handling:</strong> Optional string fields (site name) treat blank strings
 * (empty or whitespace-only) as absent and return {@link Optional#empty()}.
 *
 * <p><strong>Required Fields:</strong> {@code title} and {@code sourceUrl} are required.
 *
 * <p><strong>JSON Serialization:</strong> This class is deserialized via a private {@link
 * JsonCreator} constructor and serialized through getters. Polymorphic handling is configured at
 * the {@link RecipeCollection} interface level.
 */
public final class WebCollectionImpl implements WebCollection {

  private final String id;
  private final String title;
  private final List<Recipe> recipes;
  private final URI sourceUrl;
  private final @Nullable LocalDate dateAccessedOrNull;
  private final @Nullable String siteNameOrNull;

  /**
   * Jackson-only constructor.
   *
   * <p>This constructor is private to keep the public construction API as the builder.
   *
   * <p><strong>Validation:</strong>
   *
   * <ul>
   *   <li>{@code title} must be non-blank
   *   <li>{@code sourceUrl} must be non-null
   *   <li>{@code id} is auto-generated if null
   *   <li>{@code recipes} is defensively copied (null treated as empty)
   *   <li>Duplicate recipe IDs are rejected
   *   <li>Blank {@code siteName} is normalized to null
   * </ul>
   *
   * @param id optional ID; if null a UUID is generated
   * @param title required title (must be non-blank)
   * @param recipes list of recipes (null treated as empty)
   * @param sourceUrl required source URL
   * @param dateAccessed optional date accessed
   * @param siteName optional site name (blank treated as absent)
   * @throws IllegalArgumentException if title is blank, sourceUrl is null, or recipes contain
   *     duplicate IDs
   */
  @JsonCreator
  private WebCollectionImpl(
      @JsonProperty("id") @Nullable String id,
      @JsonProperty("title") String title,
      @JsonProperty("recipes") @Nullable List<Recipe> recipes,
      @JsonProperty("sourceUrl") @Nullable URI sourceUrl,
      @JsonProperty("dateAccessed") @Nullable LocalDate dateAccessed,
      @JsonProperty("siteName") @Nullable String siteName) {

    if (title.isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    if (sourceUrl == null) {
      throw new IllegalArgumentException("sourceUrl must not be null");
    }

    this.id = Objects.requireNonNull((id != null) ? id : UUID.randomUUID().toString());
    this.title = Objects.requireNonNull(title);
    this.recipes = Objects.requireNonNull((recipes == null) ? List.of() : List.copyOf(recipes));
    this.sourceUrl = Objects.requireNonNull(sourceUrl);
    this.dateAccessedOrNull = dateAccessed;
    this.siteNameOrNull = normalizeBlankToNull(siteName);

    ensureNoDuplicateRecipeIds(Objects.requireNonNull(this.recipes));
  }

  /**
   * Returns a new builder for creating {@link WebCollectionImpl} instances.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Normalizes a string such that blank strings are treated as absent.
   *
   * @param s input string (may be null)
   * @return trimmed string if non-blank; otherwise null
   */
  private static @Nullable String normalizeBlankToNull(@Nullable String s) {
    if (s == null) {
      return null;
    }
    String trimmed = s.trim();
    return trimmed.isBlank() ? null : trimmed;
  }

  /**
   * Validates that the given recipe list contains no duplicate IDs.
   *
   * @param recipes recipes to validate
   * @throws IllegalArgumentException if any ID appears more than once
   */
  private static void ensureNoDuplicateRecipeIds(List<Recipe> recipes) {
    HashSet<String> seen = new HashSet<>();
    for (Recipe r : recipes) {
      String rid = r.getId();
      if (!seen.add(rid)) {
        throw new IllegalArgumentException("Duplicate recipe ID in collection: " + rid);
      }
    }
  }

  /**
   * Returns the unique identifier for this collection.
   *
   * @return the unique identifier (never null)
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Returns the title of this collection.
   *
   * @return the title (never null or blank)
   */
  @Override
  public String getTitle() {
    return title;
  }

  /**
   * Returns the source type for this collection.
   *
   * @return {@link SourceType#WEBSITE}
   */
  @Override
  @JsonIgnore
  public SourceType getSourceType() {
    return SourceType.WEBSITE;
  }

  /**
   * Returns an unmodifiable list of recipes in this collection.
   *
   * <p>Recipe order is preserved and is significant for equality.
   *
   * @return an unmodifiable list of recipes (never null)
   */
  @Override
  public List<Recipe> getRecipes() {
    return recipes;
  }

  /**
   * Finds a recipe by its unique identifier.
   *
   * @param recipeId the ID of the recipe to find (must not be null)
   * @return the recipe with the given ID, or empty if not found
   */
  @Override
  public Optional<Recipe> findRecipeById(String recipeId) {
    return Objects.requireNonNull(
        recipes.stream().filter(r -> r.getId().equals(recipeId)).findFirst());
  }

  /**
   * Returns whether this collection contains a recipe with the given ID.
   *
   * @param recipeId the ID of the recipe to check for (must not be null)
   * @return true if a recipe with the given ID exists in this collection
   */
  @Override
  public boolean containsRecipe(String recipeId) {
    return findRecipeById(recipeId).isPresent();
  }

  /**
   * Returns a new web collection with the given recipe appended.
   *
   * @param recipe the recipe to add (must not be null)
   * @return a new web collection with the recipe added
   * @throws IllegalArgumentException if a recipe with the same ID already exists
   */
  @Override
  public WebCollection addRecipe(Recipe recipe) {
    if (containsRecipe(recipe.getId())) {
      throw new IllegalArgumentException(
          "Recipe with ID '" + recipe.getId() + "' already exists in this collection");
    }
    List<Recipe> newRecipes = new ArrayList<>(recipes);
    newRecipes.add(recipe);

    return WebCollectionImpl.builder()
        .id(id)
        .title(title)
        .recipes(Objects.requireNonNull(newRecipes))
        .sourceUrl(sourceUrl)
        .dateAccessed(dateAccessedOrNull)
        .siteName(siteNameOrNull)
        .build();
  }

  /**
   * Returns a new web collection with the recipe with the given ID removed.
   *
   * @param recipeId the ID of the recipe to remove (must not be null)
   * @return a new web collection with the recipe removed
   * @throws IllegalArgumentException if no recipe with the given ID exists
   */
  @Override
  public WebCollection removeRecipe(String recipeId) {
    if (!containsRecipe(recipeId)) {
      throw new IllegalArgumentException("No recipe with ID '" + recipeId + "' exists");
    }
    List<Recipe> newRecipes = recipes.stream().filter(r -> !r.getId().equals(recipeId)).toList();

    return WebCollectionImpl.builder()
        .id(id)
        .title(title)
        .recipes(Objects.requireNonNull(newRecipes))
        .sourceUrl(sourceUrl)
        .dateAccessed(dateAccessedOrNull)
        .siteName(siteNameOrNull)
        .build();
  }

  /**
   * Returns the source URL of this collection.
   *
   * @return the source URL (never null)
   */
  @Override
  public URI getSourceUrl() {
    return sourceUrl;
  }

  /**
   * Returns the date when the recipes were accessed.
   *
   * @return the date accessed, or empty if not specified
   */
  @Override
  public Optional<LocalDate> getDateAccessed() {
    return Objects.requireNonNull(Optional.<LocalDate>ofNullable(dateAccessedOrNull));
  }

  /**
   * Returns the name of the website.
   *
   * <p>Blank strings are treated as absent.
   *
   * @return the site name, or empty if not specified
   */
  @Override
  public Optional<String> getSiteName() {
    return Objects.requireNonNull(Optional.<String>ofNullable(siteNameOrNull));
  }

  /**
   * Compares this collection to another object.
   *
   * <p>Equality includes: ID, title, source type, type-specific metadata, and recipes (in order).
   *
   * @param o the object to compare
   * @return true if equal; false otherwise
   */
  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WebCollectionImpl that)) {
      return false;
    }
    return id.equals(that.id)
        && title.equals(that.title)
        && recipes.equals(that.recipes)
        && sourceUrl.equals(that.sourceUrl)
        && Objects.equals(dateAccessedOrNull, that.dateAccessedOrNull)
        && Objects.equals(siteNameOrNull, that.siteNameOrNull);
  }

  /**
   * Returns a hash code consistent with {@link #equals(Object)}.
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, title, recipes, sourceUrl, dateAccessedOrNull, siteNameOrNull);
  }

  /** Builder for creating {@link WebCollectionImpl} instances. */
  public static final class Builder {

    private @Nullable String id;
    private @Nullable String title;
    private @Nullable List<Recipe> recipes;
    private @Nullable URI sourceUrl;
    private @Nullable LocalDate dateAccessed;
    private @Nullable String siteName;

    /**
     * Sets the unique identifier.
     *
     * <p>If not set, {@link #build()} auto-generates a UUID.
     *
     * @param id unique identifier
     * @return this builder
     */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the title (required).
     *
     * <p>Per spec, this method rejects blank strings immediately.
     *
     * @param title title (must be non-blank)
     * @return this builder
     * @throws IllegalArgumentException if {@code title} is blank
     */
    public Builder title(String title) {
      if (title.isBlank()) {
        throw new IllegalArgumentException("title must not be blank");
      }
      this.title = title;
      return this;
    }

    /**
     * Sets the list of recipes.
     *
     * <p>The list is defensively copied to preserve immutability. Duplicate recipe IDs are
     * rejected.
     *
     * @param recipes recipes for the collection
     * @return this builder
     * @throws IllegalArgumentException if {@code recipes} contains duplicate recipe IDs
     */
    public Builder recipes(List<Recipe> recipes) {
      this.recipes = Objects.requireNonNull(List.copyOf(recipes));
      ensureNoDuplicateRecipeIds(Objects.requireNonNull(this.recipes));
      return this;
    }

    /**
     * Sets the source URL (required).
     *
     * @param sourceUrl source URL
     * @return this builder
     */
    public Builder sourceUrl(URI sourceUrl) {
      this.sourceUrl = sourceUrl;
      return this;
    }

    /**
     * Sets the date accessed.
     *
     * @param dateAccessed date accessed (may be null)
     * @return this builder
     */
    public Builder dateAccessed(@Nullable LocalDate dateAccessed) {
      this.dateAccessed = dateAccessed;
      return this;
    }

    /**
     * Sets the site name.
     *
     * <p>Blank strings are treated as absent when accessed via {@link #getSiteName()}.
     *
     * @param siteName site name (may be blank)
     * @return this builder
     */
    public Builder siteName(@Nullable String siteName) {
      this.siteName = siteName;
      return this;
    }

    /**
     * Builds the {@link WebCollectionImpl}.
     *
     * @return an immutable web collection
     * @throws IllegalStateException if {@code title} was not set or {@code sourceUrl} was not set
     */
    public WebCollectionImpl build() {
      if (title == null) {
        throw new IllegalStateException("title must be set");
      }
      if (sourceUrl == null) {
        throw new IllegalStateException("sourceUrl must be set");
      }
      List<Recipe> finalRecipes = Objects.requireNonNull((recipes == null) ? List.of() : recipes);
      return new WebCollectionImpl(
          id, Objects.requireNonNull(title), finalRecipes, sourceUrl, dateAccessed, siteName);
    }
  }
}
