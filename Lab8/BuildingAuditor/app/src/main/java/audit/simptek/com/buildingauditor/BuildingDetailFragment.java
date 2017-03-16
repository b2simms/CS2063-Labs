package audit.simptek.com.buildingauditor;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import audit.simptek.com.buildingauditor.dummy.BuildingListContent;

/**
 * A fragment representing a single Building detail screen.
 * This fragment is either contained in a {@link BuildingListActivity}
 * in two-pane mode (on tablets) or a {@link BuildingDetailActivity}
 * on handsets.
 */
public class BuildingDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy address this fragment is presenting.
     */
    private BuildingListContent.PropertyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BuildingDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy address specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load address from a address provider.
            mItem = BuildingListContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.address);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.building_detail, container, false);

        // Show the dummy address as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.building_detail)).setText(mItem.address);
        }

        return rootView;
    }
}
