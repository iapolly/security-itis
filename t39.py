from Crypto.Util.number import getPrime


def int_to_bytes(n):
    return n.to_bytes((n.bit_length() + 7) // 8, 'big')


def gcd(a, b):
    while b != 0:
        a, b = b, a % b

    return a


def lcm(a, b):
    return a // gcd(a, b) * b


def invmode(a, n):
    t, r = 0, n
    new_t, new_r = 1, a

    while new_r != 0:
        quotient = r // new_r
        t, new_t = new_t, t - quotient * new_t
        r, new_r = new_r, r - quotient * new_r

    if t < 0:
        t = t + n

    return t


e = 3
# key_length = 42
key_length = 1024
phi = 0

while gcd(e, phi) != 1:
    p, q = getPrime(key_length // 2), getPrime(key_length // 2)
    phi = lcm(p - 1, q - 1)
    n = p * q

d = invmode(e, phi)


def encrypt(binary_data, e, n):
    int_data = int.from_bytes(binary_data, byteorder='big')
    return pow(int_data, e, n)


def decrypt(encrypted_int_data, d, n):
    int_data = pow(encrypted_int_data, d, n)
    return int_to_bytes(int_data)


assert invmode(17, 3120) == 2753

text = b"Message message message message"
assert decrypt(encrypt(text)) == text
