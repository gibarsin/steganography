# Steganography
TPE for Cryptography & Security course from ITBA.

Java Implementation of a steganography algorithm
## Build
To build the project, it is necessary to have Maven and Java 1.8 installed.
Then, run

    $ mvn clean package

## Execution
To run the program, from the root folder

    $ java -jar target/steganography.jar <arguments>

### "help" argument
`-h` argument is a highly detailed help menu that show possible usages of the current program.
So, we highly recommend that for using this jar, you may run

    $ java -jar target/steganography.jar -h

### Usage examples

Distribute `images/database/baboon.bmp` secret image into a (14,n)-threshold scheme using the shadow files located at `images/shadows/k_14_n_19`.
`n` will be determined automatically according to the amount of shadow images in the specified directory

    $ java -jar target/steganography.jar -d -secret images/database/baboon.bmp -k 14 -dir images/shadows/k_14_n_19

Retrieve the secret as `secret.bmp` in your desktop folder using `k` shadows from the `images/shadows/k_14_n_19` directory

    $ java -jar target/steganography.jar -r -secret ~/Desktop/secret4.bmp -k 14 -dir images/shadows/k_14_n_19

## Advice
Note that this program was designed as a tool to distribute and retrieve secret files, without knowing exactly which should be the correct parameters 
that were used during the distribution of the secret (in order to correctly retrieve it).

That is why if the retrieve program is called with an invalid scheme over a set of shadow images 
(or if the specified images are not the correct shadows to retrieve the expected secret), program may fail.

## Authors
This project is written and maintained by

- [Matías Nicolás Comercio Vázquez](https://github.com/MatiasComercio)
- [Gonzalo Ibars Ingman](https://github.com/gibarsin)
- [Matías Mercado](https://github.com/MatiasMercado)

## License
    MIT License

    Copyright (c) 2017
      - Matías Nicolás Comercio Vázquez <mcomerciovazquez@gmail.com>
      - Gonzalo Ibars Ingman <gibarsin@itba.edu.ar>
      - Matías Mercado <mmercado@itba.edu.ar>

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.