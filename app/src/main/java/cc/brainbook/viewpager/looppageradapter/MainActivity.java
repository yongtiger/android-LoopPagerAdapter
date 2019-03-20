package cc.brainbook.viewpager.looppageradapter;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cc.brainbook.viewpager.transformer.CommonTransformer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///test: local images and LocalImageHolderView
        ArrayList<Integer> localImages = new ArrayList<>();
        for (int position = 0; position < 5; position++) {  ///test: page numbers is 0, 1, 2,...5 or more
            localImages.add(getResId("ic_test_" + position, R.drawable.class));
        }
        LoopPagerAdapter adapter = new LoopPagerAdapter(new ViewHolderCreator() {
            @Override
            public LocalImageViewHolder createViewHolder() {
                return new LocalImageViewHolder();
            }
        }, localImages);

        ///test: OnStartUpdateListener
        adapter.setOnStartUpdateListener(new LoopPagerAdapter.OnStartUpdateListener() {
            @Override
            public void onStartUpdate(ViewGroup container, int updatePosition, int updateCount) {
                Log.d("TAG", "LoopPagerAdapter.OnStartUpdateListener#onStartUpdate: updatePosition: " + updatePosition);
                Log.d("TAG", "LoopPagerAdapter.OnStartUpdateListener#onStartUpdate: updateCount: " + updateCount);
            }
        });

        ///test: setCanLoop
//        adapter.setCanLoop(false);


        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);


        ///test: setCurrentItem
//        viewPager.setCurrentItem(3);


        ///test: one view page may show multiple pages
        viewPager.setPadding(50, 150,100, 150);
        viewPager.setClipToPadding(false);

        ///--------- Comment out for RotateDownTransformer ---------
        final String[][] params = {
                {"PivotX", "-1", "1", "0.5", "0.5", "-1", "1"},
                {"PivotY", "-1", "1", "1", "1", "-1", "1"},

                {"Rotation", "-1", "0", "-0.05", "0", "-1", "0"},
                {"Rotation", "0", "0", "0", "0"},
                {"Rotation", "0", "1", "0", "0.05", "0", "1"},
        };

        final CommonTransformer transformer = new CommonTransformer(params);
        viewPager.setPageTransformer(false, transformer);

        viewPager.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    /**
     * Get resource ID by file name.
     *
     * @param variableName The file name.
     * @param c The class.
     * @return The resource ID.
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
