package utils;

import model.Note;
import org.json.JSONObject;

import static utils.MainPageServices.*;

public class NoteJson {
    public static JSONObject NoteToJson(Note note) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("colour", note.getColor());
        jsonBody.put("text", note.getText());
        jsonBody.put("title", note.getTitle());
        jsonBody.put("categoriesList", hashMapToJSONArray(note.getCategory()));
        jsonBody.put("figures", figureListToJSONArray(note.getFigure()));
        if(note.getGroupId() != -1)
        {
            JSONObject group = new JSONObject();
            group.put("id", note.getGroupId());
            group.put("name", note.getGroup());
            jsonBody.put("group", group);
        }
        else
        {
            jsonBody.put("group", JSONObject.NULL);
        }
        return jsonBody;
    }
    public static Note JsonToNote(JSONObject noteJson) {
        Note note = new Note(noteJson.getInt("id"),
                noteJson.getString("title"),
                noteJson.getString("text"),
                noteJson.getString("colour"),
                timestampToString(noteJson.getString("createdAt")),
                timestampToString(noteJson.getString("updatedAt")),
                noteJson.getJSONObject("user").getString("username"),
                -1,
                " ",
                jsonArrayToHashMap(noteJson.getJSONArray("categoriesList")),
                jsonArrayToFigureList(noteJson.getJSONArray("figures"))
        );
        return note;
    }
}
