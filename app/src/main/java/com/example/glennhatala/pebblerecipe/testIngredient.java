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
        ingredients = new String[]{"3 large eggs, separated", "1/4 teaspoon cream of tartar", "2 ounces cream cheese, very soft", "1 tablespoon white sugar"};
        steps = new String[5];
        steps[0] = "Preheat oven to 350 degrees F (175 degrees C). Line a baking sheet with parchment paper.";
        steps[1] = "Beat egg whites and cream of tartar together in a bowl until stiff peaks form.";
        steps[2] = "Mix egg yolks, cream cheese, and sugar together in a separate bowl using a wooden spoon and then mixing with a hand-held egg beater until mixture is very smooth and has no visible cream cheese. Gently fold egg whites into cream cheese mixture, taking care not to deflate the egg whites.";
        steps[3] = "Carefully scoop mixture onto the prepared baking sheet, forming 5 to 6 \"buns\".";
        steps[4] = "Bake in the preheated oven until cloud bread is lightly browned, about 30 minutes.";
    }
    public void fillWithTest2() {
        title = "Lemon-Buttermilk Pound Cake with Evelyn's Lemon Glaze";
        url = "http://allrecipes.com/recipe/236915/lemon-buttermilk-pound-cake-with-aunt-evelyns-lemon-glaze/?internalSource=popular&referringContentType=home%20page";
        ingredients = new String[]{"2 1/2 cups white sugar", "1 1/2 cups butter, softened ", "4 eggs", "1/2 teaspoon salt", "1/2 teaspoon baking soda",
                "1 cup buttermilk", "1 teaspoon lemon extract", "2 cups confectioners' sugar", "1/4 cup lemon juice", "2 tablespoons butter, softened", "1 tablespoon lemon zest"};
        steps = new String[6];
        steps[0] = "Preheat oven to 350 degrees F (175 degrees C). Grease and flour a fluted tube pan (such as Bundt(R)).";
        steps[1] = "Beat white sugar and 1 1/2 cups butter together in a bowl with an electric mixer until light and fluffy, about 10 minutes. Add eggs one at a time, thoroughly beating each egg into the butter mixture before adding the next.";
        steps[2] = "Sift flour, salt, and baking soda together in a bowl. Add 1/3 of the flour mixture to the butter mixture; mix well. Pour in 1/2 the buttermilk and beat until combined. Repeat adding the remaining flour mixture and buttermilk, beating well after each addition, and ending with the flour mixture. Stir lemon extract into batter. Pour batter into prepared tube pan.";
        steps[3] = "Reduce oven temperature to 325 degrees F (165 degrees C).";
        steps[4] = "Bake in the oven until a toothpick inserted into the center of the cake comes out clean, 60 to 75 minutes. Cool in the pan for 10 minutes before removing to a cake platter or plate.";
        steps[5] = "Beat confectioner's sugar, lemon juice, 2 tablespoons butter, and lemon zest together in a bowl until glaze is smooth. Pour about half the glaze over the cake; let cool. Pour remaining glaze over the cake.";
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
