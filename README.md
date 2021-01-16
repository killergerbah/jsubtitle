# jsubtitle

Bare-bones subtitle file parsing in Java. Currently supports SRT and ASS files.

## Example Usage

### ASS parsing
```
File file = new File("subtitles.ass");
AssFile assFile = AssFile.read(file);
EventSection eventSection = assFile.getEventSection();

for (DialogEvent event : eventSection.getEvents()) {
    String text = event.getText();
    long start = event.getStart();
    long end = event.getEnd();
    // ...
}
```
### SRT parsing
```
File file = new File("subtitles.srt");
List<SrtSubtitle> subtitles = SrtSubtitle.read(file);

for (SrtSubtitle subtitle : subtitles) {
    String text = subtitle.getText();
    long start = subtitle.getStart();
    long end = subtitle.getEnd();
    // ...
}
```
