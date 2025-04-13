package model.selected;

public class SelectedReadOnlyNote {

     private static SelectedReadOnlyNote selectedReadOnlyNote;
        private Integer id;
        private SelectedReadOnlyNote() {
            this.id = null;
        }
        public static SelectedReadOnlyNote getInstance() {
            if (selectedReadOnlyNote == null) {
                selectedReadOnlyNote = new SelectedReadOnlyNote();
            }
            return selectedReadOnlyNote;
        }
        public Integer getId() {
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
}
