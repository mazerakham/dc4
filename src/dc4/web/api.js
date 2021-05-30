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
