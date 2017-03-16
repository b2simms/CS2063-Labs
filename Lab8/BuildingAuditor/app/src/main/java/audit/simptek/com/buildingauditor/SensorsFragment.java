package audit.simptek.com.buildingauditor;

/**
 * Created by Brent on 2017-03-10.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import audit.simptek.com.buildingauditor.dummy.SensorListContent;

public class SensorsFragment extends Fragment{

    View inflatedView = null;

    public SensorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_sensors, container, false);

        View recyclerView = inflatedView.findViewById(R.id.sensor_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        // Inflate the layout for this fragment
        return inflatedView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SensorViewAdapter(SensorListContent.ITEMS));
    }

    public class SensorViewAdapter
            extends RecyclerView.Adapter<SensorViewAdapter.ViewHolder> {

        private final List<SensorListContent.PropertyItem> mValues;

        public SensorViewAdapter(List<SensorListContent.PropertyItem> items) {
            mValues = items;
        }

        @Override
        public SensorViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sensor_list_content, parent, false);
            return new SensorViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SensorViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).name);
            holder.mContentView.setText(mValues.get(position).address);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(),"Sensor Clicked.",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public SensorListContent.PropertyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}