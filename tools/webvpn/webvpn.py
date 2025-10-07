from binascii import hexlify, unhexlify
from Cryptodome.Cipher import AES

key = "wrdvpnisthebest!".encode('utf-8')
iv = "wrdvpnisthebest!".encode('utf-8')
host = "webvpn.hfut.edu.cn"
size = 128

def getCiphertext(plaintext: str) -> str:
    message = plaintext.encode('utf-8')
    cipher = AES.new(key, AES.MODE_CFB, iv, segment_size=size)
    encrypted = cipher.encrypt(message)
    return hexlify(encrypted).decode('utf-8')


def getPlaintext(ciphertext: str) -> str:
    message = unhexlify(ciphertext.encode('utf-8'))
    cipher = AES.new(key, AES.MODE_CFB, iv, segment_size=size)
    decrypted = cipher.decrypt(message)
    return decrypted.decode('utf-8')


def getVPNUrl(url: str) -> str:
    parts = url.split('://')
    pro = parts[0]
    add = parts[1]

    hosts = add.split('/')
    domain = hosts[0].split(':')[0]
    port = '-' + hosts[0].split(':')[1] if ':' in hosts[0] else ''
    cph = getCiphertext(domain)
    fold = '/'.join(hosts[1:])

    iv_hex = hexlify(iv).decode('utf-8')
    return f'https://{host}/{pro}{port}/{iv_hex}{cph}/{fold}'


def getOrdinaryUrl(url: str) -> str:
    parts = url.split('/')
    pro = parts[3]
    key_cph = parts[4]

    hostname = getPlaintext(key_cph[32:])
    fold = '/'.join(parts[5:])
    return f'{pro}://{hostname}/{fold}'



# 示例
if __name__ == "__main__":
    url = "http://jxglstu.hfut.edu.cn/eams5-student/home/"
    vpn_url = getVPNUrl(url)
    print("VPN URL:", vpn_url)

    ordinary_url = getOrdinaryUrl(vpn_url)
    print("Ordinary URL:", ordinary_url)