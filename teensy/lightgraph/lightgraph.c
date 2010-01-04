#include <avr/io.h>
#include <avr/pgmspace.h>
#include <stdint.h>
#include <util/delay.h>
#include "usb_serial.h"
#include "print.h"

#define SSD_CONFIG	           (DDRD = 0xFF, DDRC = 0xFF)

#define ANALOG_REF_VCC         0x40

#define CPU_PRESCALE(n)        (CLKPR = 0x80, CLKPR = (n))

void adc_config(unsigned char input);
unsigned int adc_read(void);

const unsigned char digit_table[];

int main(void) {
	
	int v = 0;
	uint8_t d = 0;

	// set for 16 MHz clock
	CPU_PRESCALE(0);
	
	// configure the seven segment displays as output devices
	SSD_CONFIG;
	
	// switch the seven segment displays off
	PORTD = 0b00000001;
	PORTC = 0b00000001;
	
	// setup analog to digital converter
   adc_config(ADC1D);
	
	// initialize the USB, and then wait for the host
	// to set configuration.  If the Teensy is powered
	// without a PC connected to the USB port, this 
	// will wait forever.
	usb_init();
	while (!usb_configured()) /* wait */ ;
	_delay_ms(1000);
	
	while (1) {
      // wait for the user to run their terminal emulator program
      // which sets DTR to indicate it is ready to receive.
      while (!(usb_serial_get_control() & USB_SERIAL_DTR)) /* wait */ ;
   
      // discard anything that was received prior.  Sometimes the
      // operating system or other software will send a modem
      // "AT command", which can still be buffered.
      usb_serial_flush_input();
      
      while (1) {
         // read the current value from analog input
         v = adc_read();
         
         // convert to a percentage out of 1024 (10 bit)
         d = (int) ((v / 1024.0) * 100.0);
         
         // update the seven segment displays
         PORTD = pgm_read_byte(digit_table + (d / 10)); // set the tens digit
         PORTC = pgm_read_byte(digit_table + (d % 10)); // set the units digit
         
         // transmit the number via USB to whoever is interested
         phex16(v);
         print("\r\n");
         usb_serial_flush_output();
         
         // wait for response
         while (!usb_serial_available());
         usb_serial_getchar();
      }
	}
}

void adc_config(unsigned char input) {
    ADCSRA = (1<<ADEN) | (1<<ADPS2) | (1<<ADPS1) | (1<<ADPS0);  // enable ADC
    ADMUX = ANALOG_REF_VCC | input;  // select Input and Reference
}

unsigned int adc_read(void) {
    unsigned char lsb;
	
    ADCSRA |= (1<<ADSC);             // begin the conversion
    while (ADCSRA & (1<<ADSC)) ;     // wait for the conversion to complete
    lsb = ADCL;                      // read the LSB first
    return (ADCH << 8) | lsb;        // read the MSB and return 10 bit result
}

// lookup table for the digits on the seven segment display
const unsigned char PROGMEM digit_table[] = {
	0b01111110,	// 0
	0b01010000,	// 1
	0b00111101,	// 2
	0b01111001,	// 3
	0b01010011,	// 4
	0b01101011,	// 5
	0b01101111,	// 6
	0b01011000,	// 7
	0b01111111,	// 8
	0b01111011,	// 9
};

// A B C D E F G .
// 3 4 6 5 2 1 0 7

