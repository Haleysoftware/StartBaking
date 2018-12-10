package com.haleysoftware.startbaking;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.startsWith;


/**
 * Created by haleysoft on 12/9/18.
 */
public class StepListTest {

    @Rule
    public ActivityTestRule<StepListActivity> activityTestRule =
            new ActivityTestRule<StepListActivity>(StepListActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    String recipeName = "Brownies";
                    String stepJson = "[{\"id\":0,\"shortDescription\":\"Recipe Introduction\",\"description\":\"Recipe Introduction\",\"videoURL\":\"https:\\/\\/d17h27t6h515a5.cloudfront.net\\/topher\\/2017\\/April\\/58ffdc33_-intro-brownies\\/-intro-brownies.mp4\",\"thumbnailURL\":\"\"},{\"id\":1,\"shortDescription\":\"Starting prep\",\"description\":\"1. Preheat the oven to 350�F. Butter the bottom and sides of a 9\\\"x13\\\" pan.\",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":2,\"shortDescription\":\"Melt butter and bittersweet chocolate.\",\"description\":\"2. Melt the butter and bittersweet chocolate together in a microwave or a double boiler. If microwaving, heat for 30 seconds at a time, removing bowl and stirring ingredients in between.\",\"videoURL\":\"https:\\/\\/d17h27t6h515a5.cloudfront.net\\/topher\\/2017\\/April\\/58ffdc43_1-melt-choclate-chips-and-butter-brownies\\/1-melt-choclate-chips-and-butter-brownies.mp4\",\"thumbnailURL\":\"\"},{\"id\":3,\"shortDescription\":\"Add sugars to wet mixture.\",\"description\":\"3. Mix both sugars into the melted chocolate in a large mixing bowl until mixture is smooth and uniform.\",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":4,\"shortDescription\":\"Mix together dry ingredients.\",\"description\":\"4. Sift together the flour, cocoa, and salt in a small bowl and whisk until mixture is uniform and no clumps remain. \",\"videoURL\":\"https:\\/\\/d17h27t6h515a5.cloudfront.net\\/topher\\/2017\\/April\\/58ffdc9e_4-sift-flower-add-coco-powder-salt-brownies\\/4-sift-flower-add-coco-powder-salt-brownies.mp4\",\"thumbnailURL\":\"\"},{\"id\":5,\"shortDescription\":\"Add eggs.\",\"description\":\"5. Crack 3 eggs into the chocolate mixture and carefully fold them in. Crack the other 2 eggs in and carefully fold them in. Fold in the vanilla.\",\"videoURL\":\"https:\\/\\/d17h27t6h515a5.cloudfront.net\\/topher\\/2017\\/April\\/58ffdc62_2-mix-egss-with-choclate-butter-brownies\\/2-mix-egss-with-choclate-butter-brownies.mp4\",\"thumbnailURL\":\"\"},{\"id\":6,\"shortDescription\":\"Add dry mixture to wet mixture.\",\"description\":\"6. Dump half of flour mixture into chocolate mixture and carefully fold in, just until no streaks remain. Repeat with the rest of the flour mixture. Fold in the chocolate chips.\",\"videoURL\":\"https:\\/\\/d17h27t6h515a5.cloudfront.net\\/topher\\/2017\\/April\\/58ffdcc8_5-mix-wet-and-cry-batter-together-brownies\\/5-mix-wet-and-cry-batter-together-brownies.mp4\",\"thumbnailURL\":\"\"},{\"id\":7,\"shortDescription\":\"Add batter to pan.\",\"description\":\"7. Pour the batter into the prepared pan and bake for 30 minutes.\",\"videoURL\":\"https:\\/\\/d17h27t6h515a5.cloudfront.net\\/topher\\/2017\\/April\\/58ffdcf4_8-put-brownies-in-oven-to-bake-brownies\\/8-put-brownies-in-oven-to-bake-brownies.mp4\",\"thumbnailURL\":\"\"},{\"id\":8,\"shortDescription\":\"Remove pan from oven.\",\"description\":\"8. Remove the pan from the oven and let cool until room temperature. If you want to speed this up, you can feel free to put the pan in a freezer for a bit.\",\"videoURL\":\"\",\"thumbnailURL\":\"\"},{\"id\":9,\"shortDescription\":\"Cut and serve.\",\"description\":\"9. Cut and serve.\",\"videoURL\":\"https:\\/\\/d17h27t6h515a5.cloudfront.net\\/topher\\/2017\\/April\\/58ffdcf9_9-final-product-brownies\\/9-final-product-brownies.mp4\",\"thumbnailURL\":\"\"}]";
                    String ingredientString = "350G Bittersweet chocolate (60-70% cacao)\n226G unsalted butter\n300G granulated sugar\n100G light brown sugar\n5UNIT large eggs\n1TBLSP vanilla extract\n140G all purpose flour\n40G cocoa powder\n1TSP salt\n350G semisweet chocolate chips";


                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent intent = new Intent(targetContext, StepListActivity.class);
                    intent.putExtra(StepListActivity.STEP_RECIPE_NAME, recipeName);
                    intent.putExtra(StepListActivity.STEP_JSON_STEPS, stepJson);
                    intent.putExtra(StepListActivity.STEP_INGREDIENTS_STRING, ingredientString);
                    return intent;
                }
            };

    private IdlingResource idlingResource;

    @Before
    public void registerIdlingResource() {
        idlingResource = activityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
    }

    @Test
    public void testStep() {
        onView(withId(R.id.step_list_fragment)).perform(actionOnItemAtPosition(5, click()));

        onView(withId(R.id.tv_direction)).check(matches(withText(startsWith("5. Crack 3 eggs"))));

        onView(withId(R.id.b_next)).perform(click());

        onView(withId(R.id.tv_direction)).check(matches(withText(startsWith("6. Dump half"))));

        onView(withId(R.id.b_previous)).perform(click());

        onView(withId(R.id.tv_direction)).check(matches(withText(startsWith("5. Crack 3 eggs"))));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }
}
