{"filter":false,"title":"bluetoothDetecttion.py","tooltip":"/bluetoothDetecttion.py","undoManager":{"mark":10,"position":10,"stack":[[{"start":{"row":0,"column":0},"end":{"row":55,"column":17},"action":"insert","lines":["import bluetooth, time","import Adafruit_BBIO.GPIO as GPIO","","search_time = 10","led_pin = \"P8_7\"","# You can hardcode the desired device ID here as a string to skip the discovery stage","addr = None","","print(\"Welcome to the Bluetooth Detection Demo! \\nMake sure your desired Bluetooth-capable device is turned on and discoverable.\")","","if addr == None:","    try:","        input(\"When you are ready to begin, press the Enter key to continue...\")","    except SyntaxError:","        pass","","    print(\"Searching for devices...\")","","    nearby_devices = bluetooth.discover_devices(duration=search_time, flush_cache=True, lookup_names=True)","","    if len(nearby_devices) > 0:","        print(\"Found %d devices!\" % len(nearby_devices))","    else:","        print(\"No devices found! Please check your Bluetooth device and restart the demo!\")","        exit(0)","","    i = 0 # Just an incrementer for labeling the list entries","    # Print out a list of all the discovered Bluetooth Devices","    for addr, name in nearby_devices:","        print(\"%s. %s - %s\" % (i, addr, name))","        i =+ 1","","    device_num = input(\"Please specify the number of the device you want to track: \")","","    # extract out the useful info on the desired device for use later","    addr, name = nearby_devices[device_num][0], nearby_devices[device_num][1]","","print(\"The script will now scan for the device %s.\" % (addr))","print(\"Feel free to move near and far away from the BeagleBone to see the state change on the LED.\\nUse Ctrl+c to exit...\")","","GPIO.setup(led_pin, GPIO.OUT)","while True:","    # Try to gather information from the desired device.","    # We're using two different metrics (readable name and data services)","    # to reduce false negatives.","    state = bluetooth.lookup_name(addr, timeout=20)","    services = bluetooth.find_service(address=addr)","    # Flip the LED pin on or off depending on whether the device is nearby","    if state == None and services == []:","        print(\"No device detected in range...\")","        GPIO.output(led_pin, GPIO.LOW)","    else:","        print(\"Device detected!\")","        GPIO.output(led_pin, GPIO.HIGH)","    # Arbitrary wait time","    time.sleep(1)"],"id":1}],[{"start":{"row":40,"column":0},"end":{"row":40,"column":1},"action":"insert","lines":["/"],"id":3}],[{"start":{"row":40,"column":1},"end":{"row":40,"column":2},"action":"insert","lines":["/"],"id":4}],[{"start":{"row":40,"column":1},"end":{"row":40,"column":2},"action":"remove","lines":["/"],"id":5}],[{"start":{"row":40,"column":0},"end":{"row":40,"column":1},"action":"remove","lines":["/"],"id":6}],[{"start":{"row":40,"column":0},"end":{"row":40,"column":1},"action":"insert","lines":["#"],"id":7}],[{"start":{"row":40,"column":1},"end":{"row":40,"column":2},"action":"insert","lines":[" "],"id":8}],[{"start":{"row":50,"column":8},"end":{"row":50,"column":9},"action":"insert","lines":["#"],"id":9}],[{"start":{"row":50,"column":9},"end":{"row":50,"column":10},"action":"insert","lines":[" "],"id":10}],[{"start":{"row":53,"column":8},"end":{"row":53,"column":9},"action":"insert","lines":["#"],"id":11}],[{"start":{"row":53,"column":9},"end":{"row":53,"column":10},"action":"insert","lines":[" "],"id":12}]]},"ace":{"folds":[],"scrolltop":165,"scrollleft":0,"selection":{"start":{"row":18,"column":4},"end":{"row":24,"column":15},"isBackwards":true},"options":{"guessTabSize":true,"useWrapMode":false,"wrapToView":true},"firstLineState":{"row":10,"state":"start","mode":"ace/mode/python"}},"timestamp":1510411837338,"hash":"ab879f33af8a8e4a2cf5fd3dcde84c08941875ca"}