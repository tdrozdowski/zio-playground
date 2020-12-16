package dev.xymox.zio

import zio.Has

package object playground {
  type Example         = Has[Example.Service]
  type AccountObserver = Has[AccountObserver.Service]
}
