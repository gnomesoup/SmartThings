/*
 * Copyright 2018 Michael J Pfammatter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

metadata{
    definition (name: "Virtual Thermostat", namespace: "gnomesoup", author: "Michael J Pfammatter") {
        capability "Actuator"
        capability "Temperature Measurement"
        capability "Thermostat"
        capability "Refresh"
        capability "Configuration"
        capability "Sensor"
        command "tempUp"
        command "tempDown"
        command "setOperatingState", ["string"]
    }
    simulator {
        // TODO: define status and reply messages here
    }

	  tiles(scale: 2) {
        multiAttributeTile(name:"thermostatFull", type:"thermostat", width:6, height:4) {
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("temp", label:'${currentValue}º', unit:"F", defaultState: true)
            }
            tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
                attributeState("VALUE_UP", action: "tempUp")
                attributeState("VALUE_DOWN", action: "tempDown")
            }
            tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
                attributeState("idle", backgroundColor:"#00A0DC")
                attributeState("heating", backgroundColor:"#e86d13")
                attributeState("cooling", backgroundColor:"#00A0DC")
            }
            tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
                attributeState("off", label:'${name}')
                attributeState("heat", label:'${name}')
                attributeState("cool", label:'${name}')
                attributeState("auto", label:'${name}')
            }
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
                attributeState("heatingSetpoint", label:'${currentValue}', unit:"F", defaultState: true)
            }
            tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
                attributeState("coolingSetpoint", label:'${currentValue}', unit:"F", defaultState: true)
            }
        }
        valueTile("heatingSetpoint", "device.heatingSetpoint", decoration: "flat", width: 2, height: 2) {
            state "heat", label:'${currentValue}', unit:"df"
        }
        valueTile("coolingSetpoint", "device.coolingSetpoint", decoration: "flat", width: 2, height: 2) {
            state "cool", label:'${currentValue}', unit:"df"
        }

    }
}

def installed() {
    log.trace "Executing 'installed'"
    initialize()
    done()
}

def configure() {
    log.trace "Executing 'configure'"
    initialize()
    done()
}

private initialize() {
    log.trace "Executing 'initialize'"
    sendEvent(name: "temperature", value: 75.0, unit: "F")
    sendEvent(name: "heatingSetpoint", value: 70.0, unit: "F")
    sendEvent(name: "coolingSetpoint", value: 80.0, unit: "F")
    sendEvent(name: "thermostatOperatingState", value: "idle")
    sendEvent(name: "thermostatMode", value: "heat")
}

private void done() {
    log.trace "---- DONE ----"
}

def tempUp() {
    // increment the setpoint when buttons are pressed on the main tile
    def tsp = device.currentValue("thermostatSetpoint") + 1
    def tMode = device.currentValue("thermostatMode")
    log.debug "Setting thermostatSetpoint to: $tsp"
    log.debug "thermostatMode = $tMode"
    sendEvent(name:"thermostatSetpoint", value: tsp, unit: "F")
    if(tMode == "heat") {
        log.debug "Setting heatingSetpoint to: $tsp"
        sendEvent(name:"thermostatSetpoint", value: tsp, unit: "F")
    }
    if(tMode == "cool") {
        log.debug "Setting coolingSetpoint to: $tsp"
        sendEvent(name:"coolingSetpoint", value: tsp, unit: "F")
    }
}

def tempDown() {
    // increment the setpoint when buttons are pressed on the main tile
    def tsp = device.currentValue("thermostatSetpoint") - 1
    def tMode = device.currentValue("thermostatMode") as String
    log.debug "Setting thermostatSetpoint to: $tsp"
    log.debug "thermostatMode = $tMode"
    sendEvent(name:"thermostatSetpoint", value: tsp, unit: "F")
    if(tMode == "heat") {
        log.debug "Setting heatingSetpoint to: $tsp"
        sendEvent(name:"thermostatSetpoint", value: tsp, unit: "F")
    }
    if(tMode == "cool") {
        log.debug "Setting coolingSetpoint to: $tsp"
        sendEvent(name:"coolingSetpoint", value: tsp, unit: "F")
    }
}

// def setOperatingState() {
// }

def setTemperature(degrees) {
    // convert temperature to double
    setTemperature(degrees.toDouble())
}

def setTemperature(Double degrees) {
    // set temperature from other source like a multi senor
    log.trace "setTemperature($degrees)"
    sendEvent(name:"temperature", value: degrees, unit: "F")
}

def setHeatingSetpoint(degress) {
    // convert temperature to double
    setHeatingSetpoint(degrees.toDouble())
}

def setHeatingSetpoint(Double degrees) {
    // Allow things like google home to adjust setpoint
    log.trace "setHeatingSetpoint($degrees)"
    sendEvent(name:"thermostatSetpoint", value: degrees, unit: "F")
    sendEvent(name:"heatingSetpoint", value: degrees, unit: "F")
}

def setCoolingSetpoint(degress) {
    // convert temperature to double
    setCoolingSetpoint(degrees.toDouble())
}

def setCoolingSetpoint(Double degrees) {
    // Allow things like google home to adjust setpoint
    log.trace "setCoolingSetpoint($degrees)"
    sendEvent(name:"thermostatSetpoint", value: degrees, unit: "F")
    sendEvent(name:"coolingSetpoint", value: degrees, unit: "F")
}
