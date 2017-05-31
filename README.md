# sensor-controller
Android component of the sensor controller

This is an Android app that can be used with Bluvision BEEKs beacons and a Dropbox accoount
for distributed estimation. To use this app, it is required to have:
1) Android device with API level minimum 23 and at least Bluetooth version 4.0
2) Bluvision BEEKs beacons
3) Source that can be estimated using observations from the beacons and depending on what 
   sensors your beacons are equipped with (e.g. light source, heat source)

To use this app, you must modify the part of the code that restricts the scanned beacons by either
removing it or providing your own beacon sIDs.

The app has 4 functions:
1) Read- used to read an excel file containing beacon sIDs and power allocations from Dropbox into the tablet (this has a specific format)
2) Scan- used to scan for beacons and displays the beacon sIDs, RSSIs (dbm), and temperatures in a list on the screen.
3) Allocate- goes through each beacon one-by-one, connects, and allocates power to them based on the power allocations provided when Read
   is pressed
4) Record- used to record sensor measurements. The resulting excel file is saved to Dropbox.

We used this app in conjunction with the MATLAB sensor controller contained in the repository sensor-controller-matlab to generate the initial 
power allocations file according to a homemade algorithm and to fuse the measurements obtained after recording.
Please see that repository's README for more information.

Don't forget to turn Bluetooth on on your device!

Please feel free to ask questions if you have any! If you decide to build on this app please share it with us.
We'd love to see what others come up with! :)

