## Installing base Raspberry OS and configuring for WeatherClock
use lite image 
use following commands after imaging new SD Card

Assumes that you have created a user called `user` with default password.

- `sudo apt-get upgdate`
- `sudo apt-get full-upgrade`
- `sudo reboot`
  

  After reboot then 
- `sudo apt-get install default-jdk` (Needs to be Java 11)
- `sudo apt-get update`
- `sudo apt-get install pigpio python-pigpio python3-pigpio`
- check version with `pigpiod -v`


- `cd`  
- `mkdir weatherclock`
- `cd weatherclock`
  
