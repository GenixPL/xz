package pw.xz.xdd.xz;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class navRoomFragment extends Fragment {


    public navRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("dla", "something");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_room, container, false);
    }

}