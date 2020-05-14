from base64 import b64decode
from t39 import int_to_bytes, decrypt, encrypt, gcd, getPrime, lcm, invmode
from math import ceil, log
from decimal import *

e = 3
key_length = 1024
phi = 0

while gcd(e, phi) != 1:
    p, q = getPrime(key_length // 2), getPrime(key_length // 2)
    phi = lcm(p - 1, q - 1)
    n = p * q

d = invmode(e, phi)


def parity_oracle_attack(text, rsa_parity_oracle, holliwood=False):
    def is_parity_odd(self, encrypted_int_data):
        return pow(encrypted_int_data, d, n) & 1

    multiplier = pow(2, e, n)

    lower_bound = Decimal(0)
    upper_bound = Decimal(n)

    k = int(ceil(log(rsa_parity_oracle.n, 2)))

    getcontext().prec = k

    for _ in range(k):
        text = (text * multiplier) % n

        if rsa_parity_oracle.is_parity_odd(text):
            lower_bound = (lower_bound + upper_bound) / 2
        else:
            upper_bound = (lower_bound + upper_bound) / 2

        if holliwood is True:
            print(int_to_bytes(int(upper_bound)))

    return int_to_bytes(int(upper_bound))


input_bytes = b64decode("VGhhdCdzIHdoeSBJIGZvdW5kIHlvdSBkb24ndCBwbGF5IG"
                        "Fyb3VuZCB3aXRoIHRoZSBGdW5reSBDb2xkIE1lZGluYQ==")

text = encrypt(input_bytes)
decrypt(text)

plaintext = parity_oracle_attack(text)
assert plaintext == input_bytes
