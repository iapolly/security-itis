from t39 import invmode, int_to_bytes, gcd, lcm, encrypt, decrypt
from Crypto.Util.number import getPrime
from random import randint


def recovery(cipher, e, n):
    while True:
        s = randint(2, n - 1)
        if s % n > 1:
            break

    new_cipher = (pow(s, e, n) * cipher) % n

    new_text = decrypt(new_cipher)
    int_text = int.from_bytes(new_text, byteorder='big')

    r = (int_text * invmode(s, n)) % n

    return int_to_bytes(r)


text = b"Message message message message"

e = 3
key_length = 1024
phi = 0

while gcd(e, phi) != 1:
    p, q = getPrime(key_length // 2), getPrime(key_length // 2)
    phi = lcm(p - 1, q - 1)
    n = p * q

cipher = encrypt(text)

recovered_text = recovery(cipher, e, n)
assert recovered_text == text
