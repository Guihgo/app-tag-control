# Tag Control

A tag controller app with NFC support

## Features
- NFC Tag management
- Inventory by NFC Tag
- Inventory with expiration date
- Multi language
- Dark / Light themes

## Enitities

### tag
	- id 			=> 0xBB3B7340
	- name 			=> Pneu Good Year 205/55
	- description 	=> Pneu para carro

### inventory
    - id            => 1
	- @Item::id		=> 0xBB3B7340
	- quantity 		=> 3
    - expiration	=> 29/04/2023 16:20

## References:

- [Material Design](https://m3.material.io)
- [Android - nfc Tag](https://developer.android.com/reference/android/nfc/Tag)
- [Android - Multi language](https://developer.android.com/training/basics/supporting-devices/languages?hl=pt-br)