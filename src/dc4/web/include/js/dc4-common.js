$.each(["get", "post", "put", "delete"], (i, method) => {
  $[method] = (url, data, ajaxOptions = {}) => {

    url = API_URL + url;
    
    const ajaxObject = {
      ...ajaxOptions,
      url: url,
      type: method,
      data: data,
      xhrFields: {
        withCredentials: true,
      },
    }
    
    return $.ajax(ajaxObject);
  };
});

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) {
    return parts.pop().split(';').shift();
  }
}
