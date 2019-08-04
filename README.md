# RekBong

An Android app to help keep track of spotted vehicle number plates (1-999).

## Features and usage

- Press FAB (+) to add new plate
    - Insert plate letters (optional) and plate number
    - With Quick Add Mode enabled, add next consecutive number
      straight to the database
        - Can also be used as a simple counter (that goes up to 999)
    - Current date and time are saved automatically
- Long-pressing a plate in main listview allows user to edit plate
  information (letters, number and date/time) or delete it completely
- Settings
    - Reverse spotting order, i.e. from 999 down to 1
    - Hide letter part from main view, so that only number part of the plate
      is shown
    - Hide date and time from the plates
    - Quick Add Mode
- Option to delete database contents
- About dialog that shows percent of spotted plates, total spotting time, and
  some info about app

## TODO

- Maybe add support for multiple plate formats
    - Some kind of Free Format mode that allows any combination of letters and
      numbers
    - In this case, support for multiple plate collections should be added as well
        - Of course, the possibility to choose between collections

## Disclaimer

The project is still very much in the early stages, so big changes and
non-functional code are expected. For now, the app supports only Finnish
registration plate format (AAA-111).

