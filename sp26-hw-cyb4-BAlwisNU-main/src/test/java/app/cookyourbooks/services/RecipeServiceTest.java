package app.cookyourbooks.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.cookyourbooks.conversion.ConversionRegistry;
import app.cookyourbooks.exception.UnsupportedConversionException;
import app.cookyourbooks.model.ExactQuantity;
import app.cookyourbooks.model.Ingredient;
import app.cookyourbooks.model.MeasuredIngredient;
import app.cookyourbooks.model.PersonalCollectionImpl;
import app.cookyourbooks.model.Recipe;
import app.cookyourbooks.model.RecipeCollection;
import app.cookyourbooks.model.Servings;
import app.cookyourbooks.model.ShoppingItem;
import app.cookyourbooks.model.ShoppingList;
import app.cookyourbooks.model.Unit;
import app.cookyourbooks.model.VagueIngredient;
import app.cookyourbooks.repository.RecipeCollectionRepository;
import app.cookyourbooks.repository.RecipeRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NullAway.Init")
class RecipeServiceTest {

  @Mock private RecipeRepository recipeRepository;
  @Mock private RecipeCollectionRepository collectionRepository;
  @Mock private ConversionRegistry conversionRegistry;

  private RecipeService service;

  @TempDir Path tempDir;

  @BeforeEach
  void setUp() {
    service = new RecipeServiceImpl(recipeRepository, collectionRepository, conversionRegistry);
  }

  // ---------------- Helpers ----------------

  RecipeCollection createCollection(String id, String title) {
    return PersonalCollectionImpl.builder().id(id).title(title).build();
  }

  Recipe createRecipe(
      String id, String title, @Nullable Servings servings, List<Ingredient> ingredients) {
    return new Recipe(id, title, servings, ingredients, List.of(), List.of());
  }

  MeasuredIngredient measured(String name, double amount, Unit unit) {
    return new MeasuredIngredient(name, new ExactQuantity(amount, unit), null, null);
  }

  void givenRecipesExist(Recipe... recipes) {
    for (Recipe r : recipes) {
      when(recipeRepository.findById(r.getId())).thenReturn(Optional.of(r));
    }
  }

  void givenCollectionExists(RecipeCollection c) {
    when(collectionRepository.findById(c.getId())).thenReturn(Optional.of(c));
  }

  // ==================== importFromText ====================

  @Test
  void testTitleFromFirstNonBlankLine() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    Recipe r = service.importFromText("MyTitle\nIngredients:\n1 cup water", "col-1");
    assertThat(r.getTitle()).isEqualTo("MyTitle");
  }

  @Test
  void testServingsParsedWithMakes() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    Recipe r = service.importFromText("Title\nMakes 10\nIngredients:\n1 cup water", "col-1");
    Servings servings = r.getServings();
    assertThat(servings).isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount()).isEqualTo(10);
  }

  @Test
  void testServingsParsedWithServes() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    Recipe r = service.importFromText("Title\nServes 4\nIngredients:\n1 cup water", "col-1");
    Servings servings = r.getServings();
    assertThat(servings).isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount()).isEqualTo(4);
  }

  @Test
  void testArticleARecognizedAsOne() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    Recipe r = service.importFromText("Title\nIngredients:\na pinch of salt", "col-1");
    assertThat(r.getIngredients()).isNotEmpty();
    MeasuredIngredient ing = (MeasuredIngredient) r.getIngredients().get(0);
    assertThat(ing.getQuantity().toDecimal()).isEqualTo(1.0);
  }

  @Test
  void testArticleAnRecognizedAsOne() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    Recipe r = service.importFromText("Title\nIngredients:\nan egg", "col-1");
    assertThat(r.getIngredients()).isNotEmpty();
    MeasuredIngredient ing = (MeasuredIngredient) r.getIngredients().get(0);
    assertThat(ing.getQuantity().toDecimal()).isEqualTo(1.0);
  }

  // ==================== importFromText ====================

  @Test
  @DisplayName("importFromText validates collection exists before parsing")
  void importFromText_missingCollectionThrowsFirst() {
    when(collectionRepository.findById("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.importFromText("Title", "missing"))
        .isInstanceOf(CollectionNotFoundException.class);

    verifyNoInteractions(recipeRepository);
  }

  @Test
  void importFromText_parsesFirstNonBlankLineAsTitle() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        My Recipe Title

        Ingredients:
        2 cups flour
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getTitle()).isEqualTo("My Recipe Title");
  }

  @Test
  void importFromText_parsesServingsWithMakes() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Recipe

        Makes 6

        Ingredients:
        2 cups flour
        """;

    Recipe r = service.importFromText(text, "col-1");

    Servings servings = r.getServings();
    assertThat(servings).isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount()).isEqualTo(6);
  }

  @Test
  void importFromText_parsesServingsWithServes() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Recipe

        Serves 8

        Ingredients:
        2 cups flour
        """;

    Recipe r = service.importFromText(text, "col-1");

    Servings servings = r.getServings();
    assertThat(servings).isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount()).isEqualTo(8);
  }

  @Test
  void importFromText_parsesArticleAAsQuantityOne() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Recipe

        Ingredients:
        a pinch of nutmeg
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getIngredients()).hasSize(1);
    Ingredient ing = r.getIngredients().get(0);
    assertThat(ing).isInstanceOf(MeasuredIngredient.class);
    MeasuredIngredient measured = (MeasuredIngredient) ing;
    assertThat(measured.getQuantity().toDecimal()).isEqualTo(1.0);
  }

  @Test
  void importFromText_parsesArticleAnAsQuantityOne() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Recipe

        Ingredients:
        an onion
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getIngredients()).hasSize(1);
    Ingredient ing = r.getIngredients().get(0);
    assertThat(ing).isInstanceOf(MeasuredIngredient.class);
    MeasuredIngredient measured = (MeasuredIngredient) ing;
    assertThat(measured.getQuantity().toDecimal()).isEqualTo(1.0);
  }

  @Test
  void importFromText_titleIsFirstNonBlankLine() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        First Line Title
        Second Line

        Ingredients:
        1 cup water
        """;

    Recipe r = service.importFromText(text, "col-1");
    assertThat(r.getTitle()).isEqualTo("First Line Title");
  }

  @Test
  void importFromText_servingsWithMakesColon() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Test Recipe

        Makes: 10

        Ingredients:
        1 cup water
        """;

    Recipe r = service.importFromText(text, "col-1");
    Servings s = r.getServings();
    assertThat(s).isNotNull();
    assertThat(java.util.Objects.requireNonNull(s).getAmount()).isEqualTo(10);
  }

  @Test
  void importFromText_servingsWithServesColon() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Test Recipe

        Serves: 5

        Ingredients:
        1 cup water
        """;

    Recipe r = service.importFromText(text, "col-1");
    Servings s = r.getServings();
    assertThat(s).isNotNull();
    assertThat(java.util.Objects.requireNonNull(s).getAmount()).isEqualTo(5);
  }

  @Test
  void importFromText_articleAWithUnit() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Test Recipe

        Ingredients:
        a cup of sugar
        """;

    Recipe r = service.importFromText(text, "col-1");
    assertThat(r.getIngredients()).hasSize(1);
    MeasuredIngredient ing = (MeasuredIngredient) r.getIngredients().get(0);
    assertThat(ing.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(ing.getQuantity().getUnit()).isEqualTo(Unit.CUP);
  }

  @Test
  void importFromText_articleAnWithoutUnit() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Test Recipe

        Ingredients:
        an egg
        """;

    Recipe r = service.importFromText(text, "col-1");
    assertThat(r.getIngredients()).hasSize(1);
    MeasuredIngredient ing = (MeasuredIngredient) r.getIngredients().get(0);
    assertThat(ing.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(ing.getQuantity().getUnit()).isEqualTo(Unit.WHOLE);
  }

  @Test
  @DisplayName("T3: importFromText must use first non-blank line as title (not second line)")
  void importFromText_titleMustBeFirstNonBlank() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    // First non-blank line is "CORRECT TITLE"
    // Second non-blank line is "Wrong Title"
    // Third non-blank line is "Ingredients:"
    // Buggy implementation might skip first line and use second or third
    String text =
        """
        CORRECT TITLE
        Wrong Title

        Ingredients:
        2 cups flour
        """;

    Recipe r = service.importFromText(text, "col-1");

    // MUST be first non-blank line, not second or any other line
    assertThat(r.getTitle())
        .as("Title must be the first non-blank line")
        .isEqualTo("CORRECT TITLE");
  }

  @Test
  @DisplayName("T4: importFromText must parse servings from 'Makes N' or 'Serves N'")
  void importFromText_servingsMustBeParsed() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Banana Bread

        Makes: 12

        Ingredients:
        2 cups flour
        """;

    Recipe r = service.importFromText(text, "col-1");

    // Buggy implementation might not parse servings at all (returns null)
    Servings servings = r.getServings();
    assertThat(servings).as("Servings should be parsed from 'Makes: 12'").isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount())
        .as("Servings amount should be 12")
        .isEqualTo(12);
  }

  @Test
  @DisplayName("T4: importFromText must parse servings from 'Serves N' format")
  void importFromText_servingsWithServesKeyword() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Pasta

        Serves: 4

        Ingredients:
        1 lb pasta
        """;

    Recipe r = service.importFromText(text, "col-1");

    Servings servings = r.getServings();
    assertThat(servings).as("Servings should be parsed from 'Serves: 4'").isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount())
        .as("Servings amount should be 4")
        .isEqualTo(4);
  }

  @Test
  @DisplayName("T10: importFromText must recognize 'a' as quantity 1")
  void importFromText_articleA_mustBeQuantityOne() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Recipe

        Ingredients:
        a pinch of nutmeg
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getIngredients()).as("Should have 1 ingredient").hasSize(1);

    Ingredient ingredient = r.getIngredients().get(0);
    assertThat(ingredient)
        .as("'a pinch of nutmeg' should be parsed as MeasuredIngredient, not VagueIngredient")
        .isInstanceOf(MeasuredIngredient.class);

    MeasuredIngredient ing = (MeasuredIngredient) ingredient;
    assertThat(ing.getName()).as("Ingredient name should be 'nutmeg'").isEqualTo("nutmeg");
    assertThat(ing.getQuantity().toDecimal())
        .as("Quantity 'a' should be recognized as 1.0")
        .isEqualTo(1.0);
    assertThat(ing.getQuantity().getUnit()).as("Unit should be PINCH").isEqualTo(Unit.PINCH);
  }

  @Test
  @DisplayName("T10: importFromText must recognize 'an' as quantity 1")
  void importFromText_articleAn_mustBeQuantityOne() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Recipe

        Ingredients:
        an onion
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getIngredients()).as("Should have 1 ingredient").hasSize(1);

    Ingredient ingredient = r.getIngredients().get(0);
    assertThat(ingredient)
        .as("'an onion' should be parsed as MeasuredIngredient, not VagueIngredient")
        .isInstanceOf(MeasuredIngredient.class);

    MeasuredIngredient ing = (MeasuredIngredient) ingredient;
    assertThat(ing.getName()).as("Ingredient name should be 'onion'").isEqualTo("onion");
    assertThat(ing.getQuantity().toDecimal())
        .as("Quantity 'an' should be recognized as 1.0")
        .isEqualTo(1.0);
    assertThat(ing.getQuantity().getUnit())
        .as("Unit should be WHOLE (no explicit unit)")
        .isEqualTo(Unit.WHOLE);
  }

  @Test
  @DisplayName("T3: importFromText title with leading blank lines")
  void importFromText_titleWithLeadingBlanks() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    // Blank lines, then first non-blank line should be title
    String text =
        """


        First Non-Blank Line

        Ingredients:
        2 cups flour
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getTitle())
        .as("Title must be 'First Non-Blank Line', the first non-blank line")
        .isEqualTo("First Non-Blank Line");
  }

  @Test
  @DisplayName("T4: importFromText servings with 'Makes' without colon")
  void importFromText_servingsMakesWithoutColon() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Cookies

        Makes 24

        Ingredients:
        2 cups flour
        """;

    Recipe r = service.importFromText(text, "col-1");

    Servings servings = r.getServings();
    assertThat(servings).as("Servings should be parsed from 'Makes 24'").isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount())
        .as("Servings amount should be 24")
        .isEqualTo(24);
  }

  @Test
  @DisplayName("T10: importFromText multiple articles a/an")
  void importFromText_multipleArticles() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Recipe

        Ingredients:
        a pinch of salt
        an apple
        a cup of water
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getIngredients()).as("Should have 3 ingredients").hasSize(3);

    // Check first ingredient: a pinch of salt
    Ingredient ing0 = r.getIngredients().get(0);
    assertThat(ing0).isInstanceOf(MeasuredIngredient.class);
    MeasuredIngredient salt = (MeasuredIngredient) ing0;
    assertThat(salt.getQuantity().toDecimal())
        .as("'a pinch' should be quantity 1.0")
        .isEqualTo(1.0);
    assertThat(salt.getQuantity().getUnit()).isEqualTo(Unit.PINCH);

    // Check second ingredient: an apple
    Ingredient ing1 = r.getIngredients().get(1);
    assertThat(ing1).isInstanceOf(MeasuredIngredient.class);
    MeasuredIngredient apple = (MeasuredIngredient) ing1;
    assertThat(apple.getQuantity().toDecimal())
        .as("'an apple' should be quantity 1.0")
        .isEqualTo(1.0);
    assertThat(apple.getQuantity().getUnit()).isEqualTo(Unit.WHOLE);

    // Check third ingredient: a cup of water
    Ingredient ing2 = r.getIngredients().get(2);
    assertThat(ing2).isInstanceOf(MeasuredIngredient.class);
    MeasuredIngredient water = (MeasuredIngredient) ing2;
    assertThat(water.getQuantity().toDecimal()).as("'a cup' should be quantity 1.0").isEqualTo(1.0);
    assertThat(water.getQuantity().getUnit()).isEqualTo(Unit.CUP);
  }

  @Test
  @DisplayName("importFromText parses 'a/an' as quantity 1 (pinch + implicit whole)")
  void importFromText_articlesAsOne() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    String text =
        """
        Title

        Ingredients:
        a pinch of nutmeg
        an onion
        a large egg
        """;

    Recipe r = service.importFromText(text, "col-1");

    assertThat(r.getIngredients()).hasSize(3);

    MeasuredIngredient nutmeg = (MeasuredIngredient) r.getIngredients().get(0);
    assertThat(nutmeg.getName()).isEqualTo("nutmeg");
    assertThat(nutmeg.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(nutmeg.getQuantity().getUnit()).isEqualTo(Unit.PINCH);

    MeasuredIngredient onion = (MeasuredIngredient) r.getIngredients().get(1);
    assertThat(onion.getName()).isEqualTo("onion");
    assertThat(onion.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(onion.getQuantity().getUnit()).isEqualTo(Unit.WHOLE);

    MeasuredIngredient egg = (MeasuredIngredient) r.getIngredients().get(2);
    assertThat(egg.getName()).isEqualTo("large egg");
    assertThat(egg.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(egg.getQuantity().getUnit()).isEqualTo(Unit.WHOLE);
  }

  @Test
  @DisplayName(
      "importFromText: title is first non-blank line (even if it's not the 'real' title); servings parsed; a/an => 1")
  void importFromText_titleIsFirstNonBlank_andServings_andArticles() throws Exception {
    RecipeCollection c = createCollection("col-1", "Test");
    givenCollectionExists(c);

    // Key trick:
    // The FIRST non-blank line is "NOT THE REAL TITLE".
    // The spec says this MUST become the recipe title.
    // Many buggy implementations mistakenly skip it and take "Chocolate Cake" instead.
    String text =
        """
        NOT THE REAL TITLE

        Chocolate Cake

        Serves: 8

        Ingredients:
        a pinch of nutmeg
        an onion
        a large egg

        Instructions:
        1. Done
        """;

    Recipe r = service.importFromText(text, "col-1");

    // ---- T3_WrongTitle: MUST use first non-blank line as title ----
    assertThat(r.getTitle()).isEqualTo("NOT THE REAL TITLE");

    // ---- T4_NoServings: MUST parse servings ----
    Servings servings = r.getServings();
    assertThat(servings).isNotNull();
    Servings nn = java.util.Objects.requireNonNull(servings);
    assertThat(nn.getAmount()).isEqualTo(8);
    // "Serves: 8" => no description
    assertThat(nn.getDescription()).isNull();

    // ---- T10_NoArticleAsOne: 'a'/'an' treated as quantity 1 ----
    assertThat(r.getIngredients()).hasSize(3);

    MeasuredIngredient nutmeg = (MeasuredIngredient) r.getIngredients().get(0);
    assertThat(nutmeg.getName()).isEqualTo("nutmeg");
    assertThat(nutmeg.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(nutmeg.getQuantity().getUnit()).isEqualTo(Unit.PINCH);

    MeasuredIngredient onion = (MeasuredIngredient) r.getIngredients().get(1);
    assertThat(onion.getName()).isEqualTo("onion");
    assertThat(onion.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(onion.getQuantity().getUnit()).isEqualTo(Unit.WHOLE);

    MeasuredIngredient egg = (MeasuredIngredient) r.getIngredients().get(2);
    assertThat(egg.getName()).isEqualTo("large egg");
    assertThat(egg.getQuantity().toDecimal()).isEqualTo(1.0);
    assertThat(egg.getQuantity().getUnit()).isEqualTo(Unit.WHOLE);

    // Still check persistence happened
    verify(recipeRepository).save(any(Recipe.class));
    verify(collectionRepository).save(any(RecipeCollection.class));
  }

  @Test
  @DisplayName(
      "importFromText parses title/servings/ingredients/instructions and saves + updates collection")
  void importFromText_happyPath() throws Exception {
    RecipeCollection c = createCollection("col-1", "Desserts");
    givenCollectionExists(c);

    String recipeText =
        """
      Chocolate Cake


      Makes 8 servings


      Ingredients:
      2 1/2 tbsp butter
      2 cups flour, sifted
      salt to taste
      2 fl oz vanilla extract
      2-3 cloves garlic


      Instructions:
      1. Mix ingredients
      2) Bake
      """;

    Recipe r = service.importFromText(recipeText, "col-1");

    ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
    ArgumentCaptor<RecipeCollection> collectionCaptor =
        ArgumentCaptor.forClass(RecipeCollection.class);

    var inOrder = inOrder(recipeRepository, collectionRepository);
    inOrder.verify(recipeRepository).save(recipeCaptor.capture());
    inOrder.verify(collectionRepository).save(collectionCaptor.capture());

    Recipe savedRecipe = recipeCaptor.getValue();
    RecipeCollection savedCollection = collectionCaptor.getValue();

    // saved recipe looks right
    assertThat(savedRecipe.getTitle()).isEqualTo("Chocolate Cake");
    Servings savedServings = savedRecipe.getServings();
    assertThat(savedServings).isNotNull();
    assertThat(java.util.Objects.requireNonNull(savedServings).getAmount()).isEqualTo(8);

    // collection update actually contains the saved/returned recipe
    assertThat(savedCollection.containsRecipe(savedRecipe.getId())).isTrue();
    assertThat(savedCollection.containsRecipe(r.getId())).isTrue();

    // returned recipe looks right
    assertThat(r.getTitle()).isEqualTo("Chocolate Cake");
    Servings servings = r.getServings();
    assertThat(servings).isNotNull();
    assertThat(java.util.Objects.requireNonNull(servings).getAmount()).isEqualTo(8);

    assertThat(r.getIngredients()).hasSize(5);
    assertThat(r.getInstructions()).hasSize(2);
    assertThat(r.getInstructions().get(0).getStepNumber()).isEqualTo(1);
    assertThat(r.getInstructions().get(0).getText()).isEqualTo("Mix ingredients");
    assertThat(r.getInstructions().get(1).getStepNumber()).isEqualTo(2);
    assertThat(r.getInstructions().get(1).getText()).isEqualTo("Bake");
  }

  // ==================== importFromJson ====================

  @Test
  @DisplayName("importFromJson saves recipe and updates collection")
  void importFromJson_happyPath() throws Exception {
    RecipeCollection c = createCollection("col-1", "Desserts");
    givenCollectionExists(c);

    Recipe test =
        new Recipe(
            "rec-1",
            "Test Recipe",
            new Servings(4),
            List.of(measured("flour", 2, Unit.CUP)),
            List.of(),
            List.of());

    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    Path file = tempDir.resolve("recipe.json");
    Files.writeString(file, mapper.writeValueAsString(test));

    Recipe imported = service.importFromJson(file, "col-1");

    ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
    ArgumentCaptor<RecipeCollection> collectionCaptor =
        ArgumentCaptor.forClass(RecipeCollection.class);

    var inOrder = inOrder(recipeRepository, collectionRepository);
    inOrder.verify(recipeRepository).save(recipeCaptor.capture());
    inOrder.verify(collectionRepository).save(collectionCaptor.capture());

    Recipe savedRecipe = recipeCaptor.getValue();
    RecipeCollection savedCollection = collectionCaptor.getValue();

    assertThat(savedRecipe.getId()).isEqualTo("rec-1");
    assertThat(savedRecipe.getTitle()).isEqualTo("Test Recipe");
    assertThat(savedCollection.containsRecipe("rec-1")).isTrue();

    assertThat(imported.getId()).isEqualTo("rec-1");
    assertThat(imported.getTitle()).isEqualTo("Test Recipe");
    assertThat(savedCollection.containsRecipe(imported.getId())).isTrue();
    verifyNoMoreInteractions(recipeRepository, collectionRepository);
  }

  @Test
  @DisplayName("importFromJson validates collection exists before reading file")
  void importFromJson_missingCollectionThrowsFirst() {
    when(collectionRepository.findById("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.importFromJson(tempDir.resolve("nope.json"), "missing"))
        .isInstanceOf(CollectionNotFoundException.class);

    verifyNoInteractions(recipeRepository);
  }

  @Test
  @DisplayName("importFromJson throws ImportException on unreadable file")
  void importFromJson_badFileThrowsImportException() {
    RecipeCollection c = createCollection("col-1", "Desserts");
    givenCollectionExists(c);

    Path missing = tempDir.resolve("does-not-exist.json");

    assertThatThrownBy(() -> service.importFromJson(missing, "col-1"))
        .isInstanceOf(ImportException.class);

    verifyNoInteractions(recipeRepository);
  }

  // ==================== scaleRecipe ====================

  @Test
  @DisplayName(
      "scaleRecipe throws IllegalArgumentException when targetServings <= 0 (before lookup)")
  void scaleRecipe_invalidTargetServings() {
    assertThatThrownBy(() -> service.scaleRecipe("rec-1", 0))
        .isInstanceOf(IllegalArgumentException.class);

    verifyNoInteractions(recipeRepository);
  }

  @Test
  @DisplayName("scaleRecipe throws RecipeNotFoundException when recipe missing")
  void scaleRecipe_missingRecipe() {
    when(recipeRepository.findById("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.scaleRecipe("missing", 8))
        .isInstanceOf(RecipeNotFoundException.class);
  }

  @Test
  @DisplayName("scaleRecipe throws IllegalArgumentException when recipe has no servings")
  void scaleRecipe_noServings() {
    Recipe r = createRecipe("rec-1", "No Servings", null, List.of(measured("flour", 2, Unit.CUP)));
    when(recipeRepository.findById("rec-1")).thenReturn(Optional.of(r));

    assertThatThrownBy(() -> service.scaleRecipe("rec-1", 8))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("scaleRecipe keeps VagueIngredients unchanged")
  void scaleRecipe_keepsVagueIngredients() {
    Recipe original =
        createRecipe(
            "rec-1",
            "Soup",
            new Servings(2),
            List.of(
                measured("water", 1, Unit.CUP),
                new VagueIngredient("salt", "to taste", null, null)));

    when(recipeRepository.findById("rec-1")).thenReturn(Optional.of(original));

    Recipe scaled = service.scaleRecipe("rec-1", 4);

    assertThat(scaled.getIngredients()).hasSize(2);

    // measured scaled
    MeasuredIngredient water = (MeasuredIngredient) scaled.getIngredients().get(0);
    assertThat(water.getQuantity().toDecimal()).isEqualTo(2.0);

    // vague preserved
    Ingredient second = scaled.getIngredients().get(1);
    assertThat(second).isInstanceOf(VagueIngredient.class);
    VagueIngredient salt = (VagueIngredient) second;
    assertThat(salt.getName()).isEqualTo("salt");
    assertThat(salt.getDescription()).isEqualTo("to taste");
  }

  @Test
  @DisplayName("scaleRecipe saves new recipe with new ID and scaled quantities")
  void scaleRecipe_happyPath() {
    Recipe original =
        createRecipe(
            "rec-1",
            "Cookies",
            new Servings(4),
            List.of(measured("flour", 2, Unit.CUP), measured("sugar", 1, Unit.CUP)));

    when(recipeRepository.findById("rec-1")).thenReturn(Optional.of(original));

    Recipe scaled = service.scaleRecipe("rec-1", 8);

    assertThat(scaled.getId()).isNotEqualTo("rec-1");
    Servings servings = scaled.getServings();
    if (servings == null) {
      throw new AssertionError("Expected servings to be non-null");
    }
    assertThat(servings.getAmount()).isEqualTo(8);

    MeasuredIngredient flour = (MeasuredIngredient) scaled.getIngredients().get(0);
    assertThat(flour.getQuantity().toDecimal()).isEqualTo(4.0);

    ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
    verify(recipeRepository).save(captor.capture());
    assertThat(captor.getValue().getId()).isNotEqualTo("rec-1");
  }

  // ==================== convertRecipe ====================

  @Test
  @DisplayName("convertRecipe throws RecipeNotFoundException when missing")
  void convertRecipe_missingRecipe() {
    when(recipeRepository.findById("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.convertRecipe("missing", Unit.GRAM))
        .isInstanceOf(RecipeNotFoundException.class);
  }

  @Test
  @DisplayName("convertRecipe propagates UnsupportedConversionException and does not save")
  void convertRecipe_unsupportedConversion() throws UnsupportedConversionException {
    Recipe r =
        createRecipe("rec-1", "Eggs", new Servings(2), List.of(measured("eggs", 2, Unit.WHOLE)));

    when(recipeRepository.findById("rec-1")).thenReturn(Optional.of(r));

    when(conversionRegistry.convert(any(), eq(Unit.GRAM), eq("eggs")))
        .thenThrow(UnsupportedConversionException.forIngredient(Unit.WHOLE, Unit.GRAM, "eggs"));

    assertThatThrownBy(() -> service.convertRecipe("rec-1", Unit.GRAM))
        .isInstanceOf(UnsupportedConversionException.class);

    verify(recipeRepository, never()).save(any());
  }

  @Test
  @DisplayName("convertRecipe keeps VagueIngredients unchanged")
  void convertRecipe_keepsVagueIngredients() throws Exception {
    Recipe r =
        createRecipe(
            "rec-1",
            "Test",
            new Servings(2),
            List.of(
                measured("flour", 2, Unit.CUP),
                new VagueIngredient("salt", "to taste", null, null)));

    when(recipeRepository.findById("rec-1")).thenReturn(Optional.of(r));

    when(conversionRegistry.convert(any(), eq(Unit.TABLESPOON), eq("flour")))
        .thenReturn(new ExactQuantity(32.0, Unit.TABLESPOON));

    Recipe converted = service.convertRecipe("rec-1", Unit.TABLESPOON);

    assertThat(converted.getIngredients()).hasSize(2);
    assertThat(converted.getIngredients().get(1)).isInstanceOf(VagueIngredient.class);
  }

  @Test
  @DisplayName("convertRecipe preserves preparation and notes on MeasuredIngredient")
  void convertRecipe_preservesMetadata() throws Exception {
    MeasuredIngredient flour =
        new MeasuredIngredient("flour", new ExactQuantity(2, Unit.CUP), "sifted", "organic");

    Recipe r = createRecipe("rec-1", "Flour", new Servings(2), List.of(flour));

    when(recipeRepository.findById("rec-1")).thenReturn(Optional.of(r));

    when(conversionRegistry.convert(any(), eq(Unit.TABLESPOON), eq("flour")))
        .thenReturn(new ExactQuantity(32.0, Unit.TABLESPOON));

    Recipe converted = service.convertRecipe("rec-1", Unit.TABLESPOON);

    MeasuredIngredient convertedFlour = (MeasuredIngredient) converted.getIngredients().get(0);
    assertThat(convertedFlour.getPreparation()).isEqualTo("sifted");
    assertThat(convertedFlour.getNotes()).isEqualTo("organic");
  }

  @Test
  @DisplayName("convertRecipe saves new recipe with converted units")
  void convertRecipe_happyPath() throws Exception {
    Recipe r =
        createRecipe("rec-1", "Flour", new Servings(2), List.of(measured("flour", 2, Unit.CUP)));

    when(recipeRepository.findById("rec-1")).thenReturn(Optional.of(r));

    when(conversionRegistry.convert(any(), eq(Unit.TABLESPOON), eq("flour")))
        .thenReturn(new ExactQuantity(32.0, Unit.TABLESPOON));

    Recipe converted = service.convertRecipe("rec-1", Unit.TABLESPOON);

    assertThat(converted.getId()).isNotEqualTo("rec-1");
    MeasuredIngredient flour = (MeasuredIngredient) converted.getIngredients().get(0);
    assertThat(flour.getQuantity().getUnit()).isEqualTo(Unit.TABLESPOON);
    assertThat(flour.getQuantity().toDecimal()).isEqualTo(32.0);

    verify(recipeRepository).save(any(Recipe.class));
  }

  // ==================== generateShoppingList ====================

  @Nested
  @DisplayName("generateShoppingList")
  class GenerateShoppingList {

    @Test
    @DisplayName(
        "combines ingredients with the same name and unit across recipes (order preserved)")
    void combinesLikeIngredients() {
      Recipe cookies =
          createRecipe(
              "rec-cookies",
              "Chocolate Chip Cookies",
              new Servings(12),
              List.of(measured("flour", 2, Unit.CUP), measured("sugar", 1, Unit.CUP)));

      Recipe cake =
          createRecipe(
              "rec-cake",
              "Chocolate Cake",
              new Servings(8),
              List.of(measured("Flour", 3, Unit.CUP), measured("sugar", 2, Unit.CUP)));

      givenRecipesExist(cookies, cake);

      ShoppingList list = service.generateShoppingList(List.of("rec-cookies", "rec-cake"));

      assertThat(list.getItems()).hasSize(2);

      ShoppingItem first = list.getItems().get(0);
      assertThat(first.getName()).isEqualTo("flour"); // first-seen name preserved
      assertThat(first.getQuantity().getUnit()).isEqualTo(Unit.CUP);
      assertThat(first.getQuantity().toDecimal()).isEqualTo(5.0);

      ShoppingItem second = list.getItems().get(1);
      assertThat(second.getName()).isEqualTo("sugar");
      assertThat(second.getQuantity().getUnit()).isEqualTo(Unit.CUP);
      assertThat(second.getQuantity().toDecimal()).isEqualTo(3.0);

      assertThat(list.getUncountableItems()).isEmpty();
    }

    @Test
    @DisplayName("keeps ingredients with the same name but different units as separate items")
    void keepsDifferentUnitsSeparate() {
      Recipe recipeA =
          createRecipe(
              "rec-a",
              "Recipe A",
              new Servings(4),
              List.of(measured("butter", 4, Unit.TABLESPOON)));

      Recipe recipeB =
          createRecipe(
              "rec-b", "Recipe B", new Servings(4), List.of(measured("butter", 1, Unit.CUP)));

      givenRecipesExist(recipeA, recipeB);

      ShoppingList list = service.generateShoppingList(List.of("rec-a", "rec-b"));

      assertThat(list.getItems()).hasSize(2);

      assertThat(list.getItems().get(0).getName()).isEqualTo("butter");
      assertThat(list.getItems().get(0).getQuantity().getUnit()).isEqualTo(Unit.TABLESPOON);
      assertThat(list.getItems().get(0).getQuantity().toDecimal()).isEqualTo(4.0);

      assertThat(list.getItems().get(1).getName()).isEqualTo("butter");
      assertThat(list.getItems().get(1).getQuantity().getUnit()).isEqualTo(Unit.CUP);
      assertThat(list.getItems().get(1).getQuantity().toDecimal()).isEqualTo(1.0);
    }

    @Test
    @DisplayName(
        "collects VagueIngredients into uncountable list (deduped case-insensitively, order preserved)")
    void uncountablesDeduped() {
      Recipe r1 =
          createRecipe(
              "rec-1",
              "R1",
              new Servings(2),
              List.of(new VagueIngredient("salt", "to taste", null, null)));

      Recipe r2 =
          createRecipe(
              "rec-2",
              "R2",
              new Servings(2),
              List.of(
                  new VagueIngredient("Salt", "to taste", null, null),
                  new VagueIngredient("fresh herbs", null, null, null)));

      givenRecipesExist(r1, r2);

      ShoppingList list = service.generateShoppingList(List.of("rec-1", "rec-2"));

      assertThat(list.getUncountableItems()).containsExactly("salt", "fresh herbs");
    }

    @Test
    @DisplayName("throws RecipeNotFoundException if any recipe ID not found")
    void throwsIfMissingRecipe() {
      when(recipeRepository.findById("ok"))
          .thenReturn(Optional.of(createRecipe("ok", "OK", new Servings(1), List.of())));
      when(recipeRepository.findById("missing")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.generateShoppingList(List.of("ok", "missing")))
          .isInstanceOf(RecipeNotFoundException.class);
    }
  }

  // ==================== findByIngredient ====================

  @Test
  @DisplayName("findByIngredient matches case-insensitive substring across all recipes")
  void findByIngredient_substringCaseInsensitive() {
    Recipe r1 =
        createRecipe(
            "rec-1",
            "Chicken Soup",
            new Servings(2),
            List.of(measured("chicken breast", 1, Unit.WHOLE)));

    Recipe r2 =
        createRecipe(
            "rec-2", "Veg Soup", new Servings(2), List.of(measured("carrots", 2, Unit.WHOLE)));

    when(recipeRepository.findAll()).thenReturn(List.of(r1, r2));

    List<Recipe> results = service.findByIngredient("CHICK");

    assertThat(results).containsExactly(r1);
  }
}
