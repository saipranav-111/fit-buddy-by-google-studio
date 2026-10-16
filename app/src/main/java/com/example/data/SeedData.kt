package com.example.data

import com.example.model.DietType
import com.example.model.FoodItem

object SeedData {
    val initialFoods = listOf(
        // Poultry & Meat
        FoodItem(
            name = "Chicken Breast (Grilled)",
            category = "Proteins",
            caloriesPer100g = 165,
            proteinPer100g = 31.0f,
            carbsPer100g = 0.0f,
            fatPer100g = 3.6f,
            defaultServingG = 150,
            servingUnitName = "fillet",
            dietType = DietType.NON_VEG,
            isPopular = true
        ),
        FoodItem(
            name = "Eggs (Whole)",
            category = "Dairy & Eggs",
            caloriesPer100g = 143,
            proteinPer100g = 12.6f,
            carbsPer100g = 0.8f,
            fatPer100g = 9.5f,
            defaultServingG = 100, // ~2 large eggs
            servingUnitName = "eggs (2 large)",
            dietType = DietType.VEGETARIAN,
            isPopular = true
        ),
        FoodItem(
            name = "Egg Whites",
            category = "Dairy & Eggs",
            caloriesPer100g = 52,
            proteinPer100g = 10.9f,
            carbsPer100g = 0.7f,
            fatPer100g = 0.2f,
            defaultServingG = 100,
            servingUnitName = "cup whites",
            dietType = DietType.VEGETARIAN,
            isPopular = false
        ),
        FoodItem(
            name = "Salmon Fillet (Baked)",
            category = "Proteins",
            caloriesPer100g = 208,
            proteinPer100g = 20.0f,
            carbsPer100g = 0.0f,
            fatPer100g = 13.0f,
            defaultServingG = 150,
            servingUnitName = "fillet",
            dietType = DietType.NON_VEG,
            isPopular = true
        ),
        FoodItem(
            name = "Canned Tuna (in Water)",
            category = "Proteins",
            caloriesPer100g = 116,
            proteinPer100g = 26.0f,
            carbsPer100g = 0.0f,
            fatPer100g = 1.0f,
            defaultServingG = 120,
            servingUnitName = "can (drained)",
            dietType = DietType.NON_VEG,
            isPopular = true
        ),
        FoodItem(
            name = "Lean Ground Beef (93/7)",
            category = "Proteins",
            caloriesPer100g = 172,
            proteinPer100g = 24.0f,
            carbsPer100g = 0.0f,
            fatPer100g = 8.0f,
            defaultServingG = 150,
            servingUnitName = "patty",
            dietType = DietType.NON_VEG,
            isPopular = false
        ),

        // Plant-Based & Legumes
        FoodItem(
            name = "Tofu (Firm)",
            category = "Proteins",
            caloriesPer100g = 83,
            proteinPer100g = 10.0f,
            carbsPer100g = 2.0f,
            fatPer100g = 5.0f,
            defaultServingG = 125,
            servingUnitName = "block slice",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Paneer / Cottage Cheese",
            category = "Dairy & Eggs",
            caloriesPer100g = 265,
            proteinPer100g = 18.0f,
            carbsPer100g = 3.5f,
            fatPer100g = 20.0f,
            defaultServingG = 100,
            servingUnitName = "bowl cubes",
            dietType = DietType.VEGETARIAN,
            isPopular = true
        ),
        FoodItem(
            name = "Low-Fat Greek Yogurt (0%)",
            category = "Dairy & Eggs",
            caloriesPer100g = 59,
            proteinPer100g = 10.0f,
            carbsPer100g = 3.6f,
            fatPer100g = 0.4f,
            defaultServingG = 170,
            servingUnitName = "cup / tub",
            dietType = DietType.VEGETARIAN,
            isPopular = true
        ),
        FoodItem(
            name = "Lentils / Yellow Dal (Cooked)",
            category = "Grains & Legumes",
            caloriesPer100g = 116,
            proteinPer100g = 9.0f,
            carbsPer100g = 20.0f,
            fatPer100g = 0.4f,
            defaultServingG = 180,
            servingUnitName = "medium bowl",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Chickpeas (Boiled)",
            category = "Grains & Legumes",
            caloriesPer100g = 164,
            proteinPer100g = 8.9f,
            carbsPer100g = 27.4f,
            fatPer100g = 2.6f,
            defaultServingG = 150,
            servingUnitName = "cup",
            dietType = DietType.VEGAN,
            isPopular = false
        ),
        FoodItem(
            name = "Black Beans (Cooked)",
            category = "Grains & Legumes",
            caloriesPer100g = 132,
            proteinPer100g = 8.9f,
            carbsPer100g = 23.7f,
            fatPer100g = 0.5f,
            defaultServingG = 150,
            servingUnitName = "cup",
            dietType = DietType.VEGAN,
            isPopular = false
        ),

        // Grains & Carbs
        FoodItem(
            name = "Rolled Oats (Raw)",
            category = "Grains & Legumes",
            caloriesPer100g = 389,
            proteinPer100g = 16.9f,
            carbsPer100g = 66.3f,
            fatPer100g = 6.9f,
            defaultServingG = 50,
            servingUnitName = "bowl / half cup",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Brown Rice (Cooked)",
            category = "Grains & Legumes",
            caloriesPer100g = 112,
            proteinPer100g = 2.6f,
            carbsPer100g = 23.5f,
            fatPer100g = 0.9f,
            defaultServingG = 150,
            servingUnitName = "cooked cup",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Basmati White Rice (Cooked)",
            category = "Grains & Legumes",
            caloriesPer100g = 130,
            proteinPer100g = 2.7f,
            carbsPer100g = 28.0f,
            fatPer100g = 0.3f,
            defaultServingG = 150,
            servingUnitName = "cooked cup",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Sweet Potato (Baked)",
            category = "Grains & Legumes",
            caloriesPer100g = 90,
            proteinPer100g = 2.0f,
            carbsPer100g = 20.7f,
            fatPer100g = 0.1f,
            defaultServingG = 150,
            servingUnitName = "medium potato",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Whole Wheat Bread",
            category = "Grains & Legumes",
            caloriesPer100g = 247,
            proteinPer100g = 12.0f,
            carbsPer100g = 41.0f,
            fatPer100g = 3.5f,
            defaultServingG = 40,
            servingUnitName = "slice",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Whole Wheat Pasta (Cooked)",
            category = "Grains & Legumes",
            caloriesPer100g = 124,
            proteinPer100g = 5.3f,
            carbsPer100g = 26.5f,
            fatPer100g = 0.5f,
            defaultServingG = 150,
            servingUnitName = "plate portion",
            dietType = DietType.VEGAN,
            isPopular = false
        ),
        FoodItem(
            name = "Quinoa (Cooked)",
            category = "Grains & Legumes",
            caloriesPer100g = 120,
            proteinPer100g = 4.4f,
            carbsPer100g = 21.3f,
            fatPer100g = 1.9f,
            defaultServingG = 140,
            servingUnitName = "cup",
            dietType = DietType.VEGAN,
            isPopular = false
        ),

        // Fruits
        FoodItem(
            name = "Banana (Fresh)",
            category = "Fruits & Veggies",
            caloriesPer100g = 89,
            proteinPer100g = 1.1f,
            carbsPer100g = 22.8f,
            fatPer100g = 0.3f,
            defaultServingG = 118,
            servingUnitName = "medium banana",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Apple (With Skin)",
            category = "Fruits & Veggies",
            caloriesPer100g = 52,
            proteinPer100g = 0.3f,
            carbsPer100g = 13.8f,
            fatPer100g = 0.2f,
            defaultServingG = 150,
            servingUnitName = "medium apple",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Blueberries (Fresh)",
            category = "Fruits & Veggies",
            caloriesPer100g = 57,
            proteinPer100g = 0.7f,
            carbsPer100g = 14.5f,
            fatPer100g = 0.3f,
            defaultServingG = 100,
            servingUnitName = "handful cup",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Strawberries",
            category = "Fruits & Veggies",
            caloriesPer100g = 32,
            proteinPer100g = 0.7f,
            carbsPer100g = 7.7f,
            fatPer100g = 0.3f,
            defaultServingG = 150,
            servingUnitName = "cup sliced",
            dietType = DietType.VEGAN,
            isPopular = false
        ),

        // Vegetables
        FoodItem(
            name = "Broccoli (Steamed)",
            category = "Fruits & Veggies",
            caloriesPer100g = 35,
            proteinPer100g = 2.4f,
            carbsPer100g = 7.2f,
            fatPer100g = 0.4f,
            defaultServingG = 100,
            servingUnitName = "cup florets",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Spinach (Fresh Baby)",
            category = "Fruits & Veggies",
            caloriesPer100g = 23,
            proteinPer100g = 2.9f,
            carbsPer100g = 3.6f,
            fatPer100g = 0.4f,
            defaultServingG = 60,
            servingUnitName = "handful salad",
            dietType = DietType.VEGAN,
            isPopular = false
        ),
        FoodItem(
            name = "Avocado",
            category = "Healthy Fats",
            caloriesPer100g = 160,
            proteinPer100g = 2.0f,
            carbsPer100g = 8.5f,
            fatPer100g = 14.7f,
            defaultServingG = 75,
            servingUnitName = "half avocado",
            dietType = DietType.VEGAN,
            isPopular = true
        ),

        // Nuts & Fats
        FoodItem(
            name = "Peanut Butter (100% Pure)",
            category = "Healthy Fats",
            caloriesPer100g = 588,
            proteinPer100g = 25.0f,
            carbsPer100g = 20.0f,
            fatPer100g = 50.0f,
            defaultServingG = 32,
            servingUnitName = "2 tbsp",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Almonds (Raw)",
            category = "Healthy Fats",
            caloriesPer100g = 579,
            proteinPer100g = 21.2f,
            carbsPer100g = 21.6f,
            fatPer100g = 49.9f,
            defaultServingG = 28,
            servingUnitName = "handful (~23 nuts)",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Extra Virgin Olive Oil",
            category = "Healthy Fats",
            caloriesPer100g = 884,
            proteinPer100g = 0.0f,
            carbsPer100g = 0.0f,
            fatPer100g = 100.0f,
            defaultServingG = 14,
            servingUnitName = "1 tbsp",
            dietType = DietType.VEGAN,
            isPopular = false
        ),
        FoodItem(
            name = "Chia Seeds",
            category = "Healthy Fats",
            caloriesPer100g = 486,
            proteinPer100g = 16.5f,
            carbsPer100g = 42.1f,
            fatPer100g = 30.7f,
            defaultServingG = 15,
            servingUnitName = "1 tbsp",
            dietType = DietType.VEGAN,
            isPopular = false
        ),

        // Fitness Supplements & Treats
        FoodItem(
            name = "Whey Protein Isolate (Vanilla)",
            category = "Snacks & Supplements",
            caloriesPer100g = 380,
            proteinPer100g = 83.3f,
            carbsPer100g = 3.3f,
            fatPer100g = 2.0f,
            defaultServingG = 30,
            servingUnitName = "1 scoop (30g)",
            dietType = DietType.VEGETARIAN,
            isPopular = true
        ),
        FoodItem(
            name = "Plant Protein Powder (Chocolate)",
            category = "Snacks & Supplements",
            caloriesPer100g = 375,
            proteinPer100g = 75.0f,
            carbsPer100g = 6.0f,
            fatPer100g = 4.5f,
            defaultServingG = 32,
            servingUnitName = "1 scoop (32g)",
            dietType = DietType.VEGAN,
            isPopular = true
        ),
        FoodItem(
            name = "Protein Bar (Chocolate Peanut)",
            category = "Snacks & Supplements",
            caloriesPer100g = 366,
            proteinPer100g = 33.3f,
            carbsPer100g = 36.6f,
            fatPer100g = 13.3f,
            defaultServingG = 60,
            servingUnitName = "bar",
            dietType = DietType.VEGETARIAN,
            isPopular = true
        ),
        FoodItem(
            name = "Dark Chocolate (85% Cocoa)",
            category = "Snacks & Supplements",
            caloriesPer100g = 598,
            proteinPer100g = 8.0f,
            carbsPer100g = 36.0f,
            fatPer100g = 46.0f,
            defaultServingG = 25,
            servingUnitName = "2 squares",
            dietType = DietType.VEGAN,
            isPopular = false
        )
    )
}
