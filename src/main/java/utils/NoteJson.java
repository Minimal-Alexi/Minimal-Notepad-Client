package utils;

import model.Note;
import org.json.JSONObject;

import static utils.MainPageServices.*;

public class NoteJson {
    public static JSONObject NoteToJson(Note note) {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("id", note.getId());
        jsonBody.put("colour", note.getColor());
        jsonBody.put("text", note.getText());
        jsonBody.put("title", note.getTitle());
        jsonBody.put("categoriesList", hashMapToJSONArray(note.getCategory()));
        jsonBody.put("figures", figureListToJSONArray(note.getFigure()));
        jsonBody.put("createdAt", note.getCreatedAt());
        jsonBody.put("updatedAt", note.getUpdatedAt());
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
        JSONObject user = new JSONObject();
        user.put("username",note.getOwner());
        jsonBody.put("user",user);
        return jsonBody;
    }
    public static Note JsonToNote(JSONObject noteJson) {
        int groupId;
        String groupName;
        if(noteJson.isNull("group"))
        {
            groupId = -1;
            groupName = "N/A";
        }
        else
        {
            JSONObject group = noteJson.getJSONObject("group");
            groupId = group.getInt("id");
            groupName = group.getString("name");
        }
        Note note = new Note(noteJson.getInt("id"),
                noteJson.getString("title"),
                noteJson.getString("text"),
                noteJson.getString("colour"),
                timestampToString(noteJson.getString("createdAt")),
                timestampToString(noteJson.getString("updatedAt")),
                noteJson.getJSONObject("user").getString("username"),
                groupId,
                groupName,
                jsonArrayToHashMap(noteJson.getJSONArray("categoriesList")),
                jsonArrayToFigureList(noteJson.getJSONArray("figures"))
        );
        return note;
    }
}
