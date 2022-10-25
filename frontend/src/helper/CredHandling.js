let performMakeCredReq = (makeCredReq) => {
    makeCredReq.challenge = base64UrlDecode(makeCredReq.challenge);
    makeCredReq.user.id = base64UrlDecode(makeCredReq.user.id);

    //Base64url decoding of id in excludeCredentials
    if (makeCredReq.excludeCredentials instanceof Array) {
        for (let i of makeCredReq.excludeCredentials) {
            if ('id' in i) {
                i.id = base64UrlDecode(i.id);
            }
        }
    }

    delete makeCredReq.status;
    delete makeCredReq.errorMessage;
    // delete makeCredReq.authenticatorSelection;

    removeEmpty(makeCredReq);

    return makeCredReq;
}

let performGetCredReq = (getCredReq) => {
    getCredReq.challenge = base64UrlDecode(getCredReq.challenge)

    //Base64url decoding of id in allowCredentials
    if (getCredReq.allowCredentials instanceof Array) {
        for (let i of getCredReq.allowCredentials) {
            if ('id' in i) {
                i.id = base64UrlDecode(i.id);
            }
        }
    }

    delete getCredReq.status;
    delete getCredReq.errorMessage;

    removeEmpty(getCredReq);

    return getCredReq;
}


/**
 * Base64 url encodes an array buffer
 * @param {ArrayBuffer} arrayBuffer
 */
function base64UrlEncode(arrayBuffer) {
    if (!arrayBuffer || arrayBuffer.length === 0) {
        return undefined;
    }

    return btoa(String.fromCharCode.apply(null, new Uint8Array(arrayBuffer)))
        .replace(/=/g, "")
        .replace(/\+/g, "-")
        .replace(/\//g, "_");
}

/**
 * Base64 url decode
 * @param {String} base64url
 */
function base64UrlDecode(base64url) {
    let input = base64url
        .replace(/-/g, "+")
        .replace(/_/g, "/");
    let diff = input.length % 4;
    if (!diff) {
        while(diff) {
            input += '=';
            diff--;
        }
    }

    return Uint8Array.from(atob(input), c => c.charCodeAt(0));
}

function removeEmpty(obj) {
    for (let key in obj) {
        if (obj[key] == null || obj[key] === "") {
            delete obj[key];
        } else if (typeof obj[key] === 'object') {
            removeEmpty(obj[key]);
        }
    }
}

export {performMakeCredReq, performGetCredReq, base64UrlEncode}
