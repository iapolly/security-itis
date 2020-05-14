import re
from hashlib import sha1
from binascii import unhexlify
from t40 import find_cube_root
from t39 import int_to_bytes, encrypt

ASN1_SHA1 = b'\x30\x21\x30\x09\x06\x05\x2b\x0e\x03\x02\x1a\x05\x00\x04\x14'


def verify(encrypted_signature, message):
    signature = b'\x00' + int_to_bytes(encrypt(encrypted_signature))

    r = re.compile(b'\x00\x01\xff+?\x00.{15}(.{20})', re.DOTALL)
    m = r.match(signature)
    if not m:
        return False

    hashed = m.group(1)
    return hashed == unhexlify(sha1(message))


message = b'hi mom'
key_length = 1024

block = b'\x00\x01\xff\x00' + ASN1_SHA1 + unhexlify(sha1(message))
garbage = (((key_length + 7) // 8) - len(block)) * b'\x00'
pre_encryption = int.from_bytes(block, byteorder='big')
forged_sig = find_cube_root(pre_encryption)
signature = int_to_bytes(forged_sig)

assert verify(signature, message)
