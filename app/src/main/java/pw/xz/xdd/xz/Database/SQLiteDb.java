package pw.xz.xdd.xz.Database;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cezary Borowski on 2018-01-13.
 */

public class SQLiteDb
{
    public void sqliteDbUpdateOnce(Context context)
    {
        SQLiteDbHelper sql = new SQLiteDbHelper(context);
        sql.onCreate(sql.getReadableDatabase());

        String name = "";
        String day = "";
        String room_id = "";
        String description = "";
        int start_time_hh = 0;
        int start_time_mm = 0;
        int end_time_hh = 0;
        int end_time_mm = 0;

        List<String[]> list = readCsv(context);

        for (int i=0; i<list.size(); i++)
        {
            name = list.get(i)[0];
            start_time_hh = Integer.parseInt(list.get(i)[1]);
            start_time_mm = Integer.parseInt(list.get(i)[2]);
            end_time_hh = Integer.parseInt(list.get(i)[3]);
            end_time_mm = Integer.parseInt(list.get(i)[4]);
            day = list.get(i)[5];
            room_id = list.get(i)[6];
            description = list.get(i)[7];
            sql.addToDb(name, start_time_hh, start_time_mm, end_time_hh,
                    end_time_mm, day, room_id, description);
        }
    }

    public final List<String[]> readCsv(Context context) {
        List<String[]> list = new ArrayList<>();
        AssetManager assetManager = context.getAssets();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(assetManager.open("dane.csv")));
            String line;
            while ((line = in.readLine()) != null) {
                String[] rowData = line.split(",");
                list.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
