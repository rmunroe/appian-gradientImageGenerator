Expression Function
===================

``linearGradientImage()``
-------------------------

This is the expression function that generates the PNG gradient image. It allows specification of many values to produce gradients that match your color schemes.

**Returns**: ``Document``

**hexColors** *(List of Text String)*: The list of colors in the gradient, as hexadecimal, e.g. ``{ "#FFAA00", "#EE5500", "#EEEEEE" }``

**distribution** *(List of Number (Floating Point))*: The distribution positions of the colors in the gradient change as a percentage (value between 0 and 1), e.g. ``{ 0.0, 0.2, 1.0 }``

**width** *(Number (Integer))*: The width of the image in pixels

**height** *(Number (Integer))*: The height of the image in pixels

**horizontal** *(Boolean)*: If true, the gradient will be oriented horizontally instead of vertically

**targetFolder** *(Folder)*: The Appian ``Folder`` to store (and cache) the image

Example
^^^^^^^
::

    a!imageField(
      label: "Result",
      labelPosition: "ABOVE",
      images: {
        a!documentImage(
          document: linearGradientImage(
            hexColors: { "#cc3300", "#ff9966", "#ffcc00", "#99cc33", "#339900" },
            distribution: { 0, 0.25, 0.5, 0.75, 1 },
            width: 500,
            height: 200,
            horizontal: false,
            targetFolder: cons!GRAD_FOLDER_DEFAULT,
          )
        )
      },
      size: "LARGE",
      isThumbnail: false,
      style: "STANDARD"
    )

This would produce an image named:
``linear_cc3300_ff9966_ffcc00_99cc33_339900_0.0_0.25_0.5_0.75_1.0_500_200_vertical.png``

.. image:: images/expression_example_result.png
   :alt: Expression example gradient

.. note::
    The filename is a concatenation of the arguments to the expression function, producing a consistently named image for the same arguments. This is how the plugin finds and returns existing images instead of regenerating them each time. As such, if you move, rename, or delete the Document, the plugin will regenerate the image using this long filename again.