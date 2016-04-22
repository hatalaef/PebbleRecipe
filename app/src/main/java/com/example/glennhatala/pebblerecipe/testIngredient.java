package com.example.glennhatala.pebblerecipe;

public class TestIngredient {
    private String title;
    private String url;
    private String[] ingredients;
    private String[] steps;

    public TestIngredient() {

    }

    public void fillWithTest() {
        title = "Easy Cloud Bread";
        url = "http://allrecipes.com/recipe/246350/easy-cloud-bread/?internalSource=staff%20pick&referringContentType=home%20page";
        ingredients = new String[]{"3 large eggs, seperated", "1/4 teaspoon cream of tartar", "2 ounces cream cheese, very soft", "1 tablespoon white surgar"};
        steps = new String[5];
        steps[0] = "Preheat oven to 350 degrees F (175 degrees C). Line a baking sheet with parchment paper.";
        steps[1] = "Beat egg whites and cream of tartar together in a bowl until stiff peaks form.";
        steps[2] = "Mix egg yolks, cream cheese, and sugar together in a separate bowl using a wooden spoon and then mixing with a hand-held egg beater until mixture is very smooth and has no visible cream cheese. Gently fold egg whites into cream cheese mixture, taking care not to deflate the egg whites.";
        steps[3] = "Carefully scoop mixture onto the prepared baking sheet, forming 5 to 6 \"buns\".";
        steps[4] = "Bake in the preheated oven until cloud bread is lightly browned, about 30 minutes.";
    }

    public String getTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }
    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getSteps() {
        return steps;
    }
}
