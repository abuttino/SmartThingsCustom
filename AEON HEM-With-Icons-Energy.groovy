/*
In-progress update of Mike Maxwell's HEM Laundry monitoring device for Aeon HEM V1. Includes customizations 
from Ogiewon and MEarly. 
SmartThings thread: https://community.smartthings.com/t/aeon-home-energy-meter-v1-read-clamps-separately/25480/100?u=danabw
Status: 
1. Done: Ogiewon - Fix reported number of buttons so both are announced/available in SmartApps  
2. Done: MEarly - Set Washer/Dryer On=pushed event; Washer/Dryer Off=held event. Allows notifications at start/end of cycle, & start/stop logging under Recent Activity.     
2. Done: Danabw - All Preferences entries labeled 
3. Need help: 1) Add reporting frequency setting to preferences (currently only managed in Configuration section, not available in preferences
4. Need help: 2) ID what KWhDelay and detailDelay settings control, and remove them from Preferences if user control not necessary  
*/

metadata {
	// Automatically generated. Make future change here.
	definition (
		name: 		"Aeon HEM Split Reading with Icons", 
		namespace: 	"abuttino",
		category: 	"Green Living",
		author: 	"abuttino"
	)  
    
	{
		capability "Configuration"
		capability "Switch"
        capability "Button"
        capability "Energy Meter"
		capability "Power Meter"
        capability "Actuator"
		capability "Holdable Button"
		capability "Sensor"

        attribute "washerWatts", "string"
        attribute "dryerWatts", "string"
        attribute "washerState", "string"
        attribute "dryerState", "string"
        
//        command "configure"
        attribute "energy", "string"
        attribute "power", "string"
        attribute "volts", "string"
        attribute "voltage", "string"		// We'll deliver both, since the correct one is not defined anywhere
        
        attribute "energyDisp", "string"
        attribute "energyOne", "string"
        attribute "energyTwo", "string"
        
        attribute "powerDisp", "string"
        attribute "powerOne", "string"
        attribute "powerTwo", "string"

        command "reset"
        command "configure"
        command "refresh"
        command "poll"
        
		fingerprint deviceId: "0x2101", inClusters: " 0x70,0x31,0x72,0x86,0x32,0x80,0x85,0x60"
	}

	preferences {
        input "c1Name", type: "text", title: "Clamp 1 device (e.g., Washer)", description: "", required: true
       	input "washerRW", type: "number", title: "Minimum watts device draws when running:", description: "", required: true
        input "c2Name", type: "text", title: "Clamp 2 device (e.g., Dryer)", description: "", required: true
        input "dryerRW", type: "number", title: "Minimum watts device draws when running:", description: "", required: true
        input "voltageValue", type: "number", title: "Line voltage: 120 or 240", description: "", required: true
        input "kWhCost", type: "number", title: "Cost per kWh", description: "", required: true   
		input "kWhDelay", type: "number", title: "kWh Delay", description: "", required: true   
		input  "detailDelay", type: "number", title: "Detail Delay", description: "", required: true
    	input "wDelay", "number", title: "Power report seconds (30)", /* description: "30", */ defaultValue: 30


    }
	
	simulator {
		for (int i = 0; i <= 10000; i += 1000) {
			status "power  ${i} W-ZZ": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 33, scale: 2, size: 4).incomingMessage()
		}
		for (int i = 0; i <= 100; i += 10) {
			status "energy  ${i} kWh-ZZ": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 33, scale: 0, size: 4).incomingMessage()
		}
        // TODO: Add data feeds for Volts and Amps
	}

	tiles(scale: 2) {
    	multiAttributeTile(name:"laundryState", type: "generic", width: 6, height: 4, canChangeIcon: false){
        	tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
            	attributeState "on", label:'', icon:"st.samsung.da.dryer_ic_dryer", backgroundColor:"#79b821"
            	attributeState "off", label:'', icon:"st.samsung.da.dryer_ic_dryer", backgroundColor:"#ffffff"
        	}
                   
            tileAttribute("device.switch", key: "SECONDARY_CONTROL") {
             	attributeState "on", label:'Laundry Running'
            	attributeState "off", label:'Laundry Done'
    		}

        }
    // Watts row
		valueTile("powerDisp", "device.powerDisp") {
			state (
				"default", 
				label:'${currentValue}', 
            	foregroundColors:[
            		[value: 1, color: "#000000"],
            		[value: 10000, color: "#ffffff"]
            	], 
            	foregroundColor: "#000000"
			)
		}
        valueTile("powerOne", "device.powerOne") {
        	state(
        		"default", 
        		label:'${currentValue}', 
            	foregroundColors:[
            		[value: 1, color: "#000000"],
            		[value: 10000, color: "#ffffff"]
            	], 
            	foregroundColor: "#000000"
			)
        }
        valueTile("powerTwo", "device.powerTwo") {
        	state(
        		"default", 
        		label:'${currentValue}', 
            	foregroundColors:[
            		[value: 1, color: "#000000"],
            		[value: 10000, color: "#ffffff"]
            	], 
            	foregroundColor: "#000000"
			)
        }

	// Power row
		valueTile("energyDisp", "device.energyDisp") {
			state(
				"default", 
				label: '${currentValue}',		// shows kWh value with ' kWh' suffix
				foregroundColor: "#000000", 
				backgroundColor: "#ffffff")
		}
        valueTile("energyOne", "device.energyOne") {
        	state(
        		"default", 
        		label: '${currentValue}',		// shows kWh value with ' kWh' suffix
        		foregroundColor: "#000000", 
        		backgroundColor: "#ffffff")
        }        
        valueTile("energyTwo", "device.energyTwo") {
        	state(
        		"default", 
        		label: '${currentValue}',		// shows kWh value with ' kWh' suffix
        		foregroundColor: "#000000", 
        		backgroundColor: "#ffffff")
        }
       
		valueTile("power", "device.power") {
			state (
				"default", 
				label:'${currentValue}W', icon:"st.samsung.da.dryer_ic_dryer", 
                foregroundColor: "#000000", 
                backgroundColor:"#79b821"
			)
		}
        valueTile("energy", "device.energy") {
			state (
				"default", 
				label:'${currentValue}kWh', 
                foregroundColor: "#000000", 
                backgroundColor:"#79b821"
			)
		}

        standardTile("washerState", "device.washerState", width: 3, height: 3, canChangeIcon: true) {
        	state "off", label:'${name}', icon: "st.samsung.da.washer_ic_washer", backgroundColor:"#ffffff"
            state "on", label:'${name}', icon: "st.samsung.da.washer_ic_washer", backgroundColor:"#79b821"
        }
        standardTile("dryerState", "device.dryerState", width: 3, height: 3, canChangeIcon: true) {
        	state "off", label:'${name}', icon: "st.samsung.da.dryer_ic_dryer", backgroundColor:"#ffffff"
            state "on", label:'${name}', icon: "st.samsung.da.dryer_ic_dryer", backgroundColor:"#79b821"
        }

		valueTile("washer", "device.washerWatts", width: 3, height: 2, decoration: "flat") {
            state("default", label:'Washer\n${currentValue} Watts', foregroundColor: "#000000")
        }

		valueTile("dryer", "device.dryerWatts", width: 3, height: 2, decoration: "flat") {
            state("default", label:'Dryer\n${currentValue} Watts', foregroundColor: "#000000")
        }
       
		standardTile("configure", "device.configure", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}

		main "power"
		details(["laundryState","washerState","dryerState","washer","dryer","configure"])
	}
}

def installed() {
	reset()						// The order here is important
	configure()					// Since reports can start coming in even before we finish configure()
	refresh()
}

def updated() {
	configure()
	resetDisplay()
	refresh()
}
def parse(String description) {
	def result = null
	def cmd = zwave.parse(description, [0x31: 1, 0x32: 1, 0x60: 3])
	if (cmd) {
		result = createEvent(zwaveEvent(cmd))
	}
	if (result) { 
		log.debug "Parse returned ${result?.descriptionText}"
		return result
	} else {
	}
}
def zwaveEvent(physicalgraph.zwave.commands.meterv1.MeterReport cmd) {
    def dispValue
    def newValue
    def formattedValue
    
	//def timeString = new Date().format("h:mm a", location.timeZone)
    
    if (cmd.meterType == 33) {
		if (cmd.scale == 0) {
        	newValue = Math.round(cmd.scaledMeterValue * 100) / 100
        	if (newValue != state.energyValue) {
        		formattedValue = String.format("%5.2f", newValue)
    			dispValue = "Total\n${formattedValue}\nkWh"		// total kWh label
                sendEvent(name: "energyDisp", value: dispValue as String, unit: "", descriptionText: "Display Energy: ${newValue} kWh", displayed: false)
                state.energyValue = newValue
                [name: "energy", value: newValue, unit: "kWh", descriptionText: "Total Energy: ${formattedValue} kWh"]

            }
		} 
		else if (cmd.scale==2) {
        	newValue = Math.round(cmd.scaledMeterValue*10)/10
            formattedValue = String.format("%5.1f", newValue)
        	//newValue = Math.round(cmd.scaledMeterValue)		// really not worth the hassle to show decimals for Watts
        	if (newValue != state.powerValue) {
    			dispValue = "Total\n"+newValue+"\nWatts"	// Total watts label
                sendEvent(name: "powerDisp", value: dispValue as String, unit: "", descriptionText: "Display Power: ${newValue} Watts", displayed: false)
                state.powerValue = newValue
                [name: "power", value: newValue, unit: "W", descriptionText: "Total Power: ${formattedValue} Watts"]
           }
		}
 	}     
}
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    def dispValue
    def newValue
    def formattedValue
	//log.info "mc3v cmd: ${cmd}"
	if (cmd.commandClass == 50) {    
   		def encapsulatedCommand = cmd.encapsulatedCommand([0x30: 1, 0x31: 1]) // can specify command class versions here like in zwave.parse
		if (encapsulatedCommand) {
			if (cmd.sourceEndPoint == 1) {
				if (encapsulatedCommand.scale == 2 ) {
					newValue = Math.round(encapsulatedCommand.scaledMeterValue*10)/10
                    formattedValue = String.format("%5.1f", newValue)
					dispValue = "${c1Name}\n${formattedValue}\nWatts"	// L1 Watts Label
					if (newValue != state.powerL1) {
						state.powerL1 = newValue
						[name: "powerOne", value: dispValue, unit: "", descriptionText: "L1 Power: ${formattedValue} Watts"]
					}	
				} 
				else if (encapsulatedCommand.scale == 0 ){
					newValue = Math.round(encapsulatedCommand.scaledMeterValue * 100) / 100
					formattedValue = String.format("%5.2f", newValue)
					dispValue = "${c1Name}\n${formattedValue}\nkWh"		// L1 kWh label
					if (newValue != state.energyL1) {
						state.energyL1 = newValue
						[name: "energyOne", value: dispValue, unit: "", descriptionText: "L1 Energy: ${formattedValue} kWh"]
					}
				}
			} 
			else if (cmd.sourceEndPoint == 2) {
				if (encapsulatedCommand.scale == 2 ){
					newValue = Math.round(encapsulatedCommand.scaledMeterValue*10)/10
                    formattedValue = String.format("%5.1f", newValue)
                    dispValue = "${c2Name}\n${formattedValue}\nWatts"	// L2 Watts Label
					if (newValue != state.powerL1) {
						state.powerL2 = newValue
						[name: "powerTwo", value: dispValue, unit: "", descriptionText: "L2 Power: ${formattedValue} Wa1tts"]
					}	
				} 
				else if (encapsulatedCommand.scale == 0 ){
					newValue = Math.round(encapsulatedCommand.scaledMeterValue * 100) / 100
					formattedValue = String.format("%5.2f", newValue)
					dispValue = "${c2Name}\n${formattedValue}\nkWh"		// L2 kWh label
					if (newValue != state.energyL2) {
						state.energyL2 = newValue
						[name: "energyTwo", value: dispValue, unit: "", descriptionText: "L2 Energy: ${formattedValue} kWh"]
					}
				} 
			}
		}
	}
	if (cmd.commandClass == 50) {  
    	def encapsulatedCommand = cmd.encapsulatedCommand([0x30: 1, 0x31: 1])
        if (encapsulatedCommand) {
        	def scale = encapsulatedCommand.scale
        	def value = encapsulatedCommand.scaledMeterValue
            def source = cmd.sourceEndPoint
            def str = ""
            def name = ""
        	if (scale == 2 ){ //watts
            	str = "watts"
                if (source == 1){
                	name = "washerWatts"
                    if (value >= settings.washerRW.toInteger()){
                    	//washer is on
                        sendEvent(name: "washerState", value: "on", displayed: true)
                        
                    //button event
                    if (!state.washerIsRunning)
                    	sendEvent(name: "button", value: "pushed", data: [buttonNumber: 1], descriptionText: "Washer has started.", isStateChange: true)                        
                        
                        state.washerIsRunning = true
                    } else {
                    	//washer is off
                        if (state.washerIsRunning == true){
                        	//button event
                            sendEvent(name: "button", value: "held", data: [buttonNumber: 1], descriptionText: "Washer has finished.", isStateChange: true)
                        }
                        sendEvent(name: "washerState", value: "off", displayed: false)
                        state.washerIsRunning = false
                    }
                } else {
                	name = "dryerWatts"
                    if (value >= settings.dryerRW.toInteger()){
                    	//dryer is on
                        sendEvent(name: "dryerState", value: "on", displayed: false)
                        
                    //button event
                    if (!state.dryerIsRunning)
                    	sendEvent(name: "button", value: "pushed", data: [buttonNumber: 2], descriptionText: "Dryer has started.", isStateChange: true)                        
                        
                        state.dryerIsRunning = true
                    } else {
                    	//dryer is off
                        if (state.dryerIsRunning == true){
                        	//button event
                            sendEvent(name: "button", value: "held", data: [buttonNumber: 2], descriptionText: "Dryer has finished.", isStateChange: true)
                        }
                        sendEvent(name: "dryerState", value: "off", displayed: false)
                        state.dryerIsRunning = false
                    }
                }
                if (state.washerIsRunning || state.dryerIsRunning){
                	sendEvent(name: "switch", value: "on", descriptionText: "Washer and/or Dryer running...", displayed: true)
                } else {
                	sendEvent(name: "switch", value: "off", displayed: false)
                }
                //log.debug "mc3v- name: ${name}, value: ${value}, unit: ${str}"
            	return [name: name, value: value.toInteger(), unit: str, displayed: false]
            }
        }
    }
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [:]
	map.name = "battery"
	map.unit = "%"
   
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} battery is low"
		map.isStateChange = true
	} 
	else {
		map.value = cmd.batteryLevel
	}
	log.debug map
	return map
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
    log.debug "Unhandled event ${cmd}"
	[:]
}

def refresh() {			// Request HEMv2 to send us the latest values for the 4 we are tracking
	log.debug "refresh()"
    
	delayBetween([
		zwave.meterV2.meterGet(scale: 0).format(),		// Change 0 to 1 if international version
		zwave.meterV2.meterGet(scale: 2).format(),
	])
    resetDisplay()
}

def poll() {
	log.debug "poll()"
	refresh()
}

def resetDisplay() {
	log.debug "resetDisplay()"
	
    sendEvent(name: "powerDisp", value: "Total\n" + state.powerValue + "\nWatts", unit: "W")	
    sendEvent(name: "energyDisp", value: "Total\n" + state.energyValue + "\nkWh", unit: "kWh")
	sendEvent(name: "powerOne", value: c1Name + "\n" + state.powerL1 + "\nWatts", unit: "W")    
    sendEvent(name: "energyOne", value: c1Name + "\n" + state.energyL1 + "\nkWh", unit: "kWh")	
    sendEvent(name: "powerTwo", value: c2Name + "\n" + state.powerL2 + "\nWatts", unit: "W")
    sendEvent(name: "energyTwo", value: c2Name + "\n" + state.energyL2 + "\nkWh", unit: "kWh")
}

def reset() {
	log.debug "reset()"
	
    state.energyValue = ""
	state.powerValue = ""
    state.energyL1 = ""
    state.energyL2 = ""
    state.powerL1 = ""
    state.powerL2 = ""
	
    resetDisplay()
    
	return [
		zwave.meterV2.meterReset().format(),
		zwave.meterV2.meterGet(scale: 0).format()
	]
    
    configure()
}

def configure() {
	log.debug "configure()"
    
	Long kwhDelay = settings.kWhDelay as Long
    Long wDelay = settings.wDelay as Long
    
    if (kwhDelay == null) {		// Shouldn't have to do this, but there seem to be initialization errors
		kwhDelay = 15
	}

	if (wDelay == null) {
		wDelay = 15
	}
    
	def cmd = delayBetween([
    	zwave.configurationV1.configurationSet(parameterNumber: 1, size: 2, scaledConfigurationValue: voltageValue).format(),		// assumed voltage
		zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: 0).format(),			// Disable (=0) selective reporting
		zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: 1).format(),			// Don't send whole HEM unless watts have changed by 30
		zwave.configurationV1.configurationSet(parameterNumber: 5, size: 2, scaledConfigurationValue: 1).format(),			// Don't send L1 Data unless watts have changed by 15
		zwave.configurationV1.configurationSet(parameterNumber: 6, size: 2, scaledConfigurationValue: 1).format(),			// Don't send L2 Data unless watts have changed by 15 
        zwave.configurationV1.configurationSet(parameterNumber: 8, size: 1, scaledConfigurationValue: 5).format(),			// Or by 5% (whole HEM)
		zwave.configurationV1.configurationSet(parameterNumber: 9, size: 1, scaledConfigurationValue: 5).format(),			// Or by 5% (L1)
	    zwave.configurationV1.configurationSet(parameterNumber: 10, size: 1, scaledConfigurationValue: 5).format(),			// Or by 5% (L2)
		zwave.configurationV1.configurationSet(parameterNumber: 100, size: 4, scaledConfigurationValue: 1).format(),		// reset to defaults
        zwave.configurationV1.configurationSet(parameterNumber: 110, size: 4, scaledConfigurationValue: 1).format(),		// reset to defaults
		zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 772).format(),		// watt
		zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: wDelay).format(), 	// every %Delay% seconds
        zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 6152).format(),   	// kwh
		zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: kwhDelay).format(), // Every %Delay% seconds
		zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 1).format(),		// battery
		zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: 10).format() 		// every hour
	], 2000)
	
    return cmd
	log.debug cmd
	cmd
}
