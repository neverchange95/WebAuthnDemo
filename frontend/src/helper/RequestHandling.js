function restPost(endpointURL, object) {
    return fetch(endpointURL, {
        method: 'POST',
        credentials: "include",
        body: JSON.stringify(object),
        headers: {
            'content-type': 'application/json',
        },
    })
        .then(response => {
            return response.json()
        })
}

export {restPost}



