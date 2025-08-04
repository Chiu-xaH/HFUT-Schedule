from argparse import ArgumentParser
from binascii import hexlify, unhexlify
from Crypto.Cipher import AES


def getCiphertext(plaintext, key, cfb_iv, size = 128):
    '''From plantext hostname to ciphertext'''
    
    message = plaintext.encode('utf-8')
    
    cfb_cipher_encrypt = AES.new(key, AES.MODE_CFB, cfb_iv, segment_size = size)       # Must include segment_size
    mid = cfb_cipher_encrypt.encrypt(message)

    return hexlify(mid).decode()


def getPlaintext(ciphertext, key, cfb_iv, size = 128):
    '''From ciphertext hostname to plaintext'''
    
    message = unhexlify(ciphertext.encode('utf-8'))
    
    cfb_cipher_decrypt = AES.new(key, AES.MODE_CFB, cfb_iv, segment_size = size)
    cfb_msg_decrypt = cfb_cipher_decrypt.decrypt(message).decode('utf-8')
    
    return cfb_msg_decrypt


def getVPNUrl(url):
    '''From ordinary url to webVPN url'''

    parts = url.split('://')
    pro = parts[0]
    add = parts[1]
    
    hosts = add.split('/')
    domain = hosts[0].split(':')[0]
    port = '-' + hosts[0].split(':')[1] if ":" in hosts[0] else ''
    cph = getCiphertext(domain, key=key_, cfb_iv=iv_)
    fold = '/'.join(hosts[1:])

    key = hexlify(iv_).decode('utf-8')
    
    return 'https://' + institution + '/' + pro + port + '/' + key + cph + '/' + fold


def getOrdinaryUrl(url):
    '''From webVPN url to ordinary url'''

    parts = url.split('/')
    pro = parts[3]
    key_cph = parts[4]
    
    hostname = getPlaintext(key_cph[32:], key=key_, cfb_iv=iv_)
    fold = '/'.join(parts[5:])

    return pro + "://" + hostname + '/' + fold


if __name__ == '__main__':
    parser = ArgumentParser(description="convert between webVPN url and ordinary url")
    parser.add_argument("url", help="url to be converted")
    codec = parser.add_mutually_exclusive_group(required=True)
    codec.add_argument("-e", "--encode", action="store_true", help="convert ordinary url to webVPN url")
    codec.add_argument("-d", "--decode", action="store_true", help="convert webVPN url to ordinary url")
    parser.add_argument("--key", default="wrdvpnisthebest!", help="crypto key")
    parser.add_argument("--iv", default="wrdvpnisthebest!", help="crypto iv")
    parser.add_argument("-i", "--institution", default="webvpn.hfut.edu.cn", help="hostname of institution (only work at encode mode)")

    args = parser.parse_args()
    url = args.url
    key_ = args.key.encode('utf-8')
    iv_ = args.iv.encode('utf-8')

    if args.encode:
        institution = args.institution
        print(getVPNUrl(url))
    elif args.decode:
        institution = url.split('/')[2]
        print(getOrdinaryUrl(url))
